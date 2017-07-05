package com.ge.predix.cyber.csam.ehsender.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ge.predix.cyber.csam.ehsender.entities.EventHubConnection;
import com.ge.predix.cyber.csam.ehsender.entities.SenderDetails;
import com.ge.predix.eventhub.Ack;
import com.ge.predix.eventhub.AckStatus;
import com.ge.predix.eventhub.EventHubClientException;
import com.ge.predix.eventhub.client.Client;
import com.ge.predix.eventhub.client.Client.PublishCallback;
import com.ge.predix.eventhub.configuration.EventHubConfiguration;
import com.ge.predix.eventhub.configuration.PublishAsyncConfiguration;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SenderServiceImpl implements SenderService {

	@Autowired
    private EventHubConnection target;
	
	@Autowired
    private StatsService stats;

	private AtomicReference<Object> message = new AtomicReference<>();
    private AtomicLong speed = new AtomicLong();
    private AtomicLong timeUnitInNanos = new AtomicLong();
    private AtomicLong amount = new AtomicLong(Long.MAX_VALUE);
    private AtomicInteger successPerTimeUnitCount = new AtomicInteger(0);
    private AtomicBoolean isStop = new AtomicBoolean(true);
    private AtomicBoolean isAlreadyStarted = new AtomicBoolean(false);
    private AtomicBoolean isClientClosed = new AtomicBoolean(false);
    
    private String timeUnitStr;
    private String replaceWithTime;
    private SimpleDateFormat sdf;
    private CountDownLatch latch;
    private ExecutorService executor;
    private Client client;
    
    @PostConstruct
    public void init() throws EventHubClientException {
    	sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
    	initClient();
    }
    
	private void initClient() throws EventHubClientException {
		EventHubConfiguration configuration = new EventHubConfiguration.Builder()
                .authURL(target.getAuthURL())
                .clientID(target.getClientId())
                .clientSecret(target.getClientSecret())
                .host(target.getHost())
                .port(target.getPort())
                .zoneID(target.getEventhubZoneId())
                .publishConfiguration(new PublishAsyncConfiguration.Builder().build())
                .build();
        Client ehClient = new Client(configuration);
        ehClient.registerPublishCallback(new PublishCallback() {
			
			@Override
			public void onFailure(Throwable throwable) {
				log.error(String.format("new callback onFailure from eventHub. error %s", throwable));
                if (throwable.getMessage() != null && throwable.getMessage().contains("FAILED_PRECONDITION")){
                	client.shutdown();
                	isClientClosed.set(true);
                }
                
                stop();
			}
			
			@Override
			public void onAck(List<Ack> acks) {
				for (Ack ack : acks) {
					if (ack.getStatusCode().equals(AckStatus.ACCEPTED)) {
						successPerTimeUnitCount.incrementAndGet();
						stats.incrementAndGetAccepted();
					} else {
						stats.incrementAndGetFailed();
					}
				}
			}
		});
        
        client = ehClient;
        isClientClosed.set(false);
        
	}

	@Override
	public String start(SenderDetails details) throws EventHubClientException {
		setDetails(details);
		return startWithLocalDetails();
	}
	
	private String startWithLocalDetails() throws EventHubClientException {
		log.info("Trying to start the sender sender.");
		if (isAlreadyStarted.compareAndSet(false, true)) {
			if(isClientClosed.get()) {
				initClient();
            }
			
			latch = new CountDownLatch(1);
            isStop.set(false);
            stats.clearAllCounters();
            executor = Executors.newFixedThreadPool(1);
            executor.submit(new Publisher());
            
			return "Started.";
		} else {
			log.error("The sender is already started.");
			return "The sender is already started. Please stop, and try again.";
		}
	}
	
	private void setDetails(SenderDetails details) {
		message.set(details.getMessage());
		speed.set(details.getSpeed());
		timeUnitInNanos.set(details.getTimeUnit().toNano());
		amount.set(details.getAmount() == 0 ? Long.MAX_VALUE : details.getAmount());
		replaceWithTime = details.getMessage().contains(details.getReplaceWithTime()) ? details.getReplaceWithTime() : "";
		timeUnitStr = details.getTimeUnit().name().toLowerCase();
	}

	@Override
	public void stop() {
		log.info("Trying to stop sender.");
        isStop.set(true);
        
        if(latch != null) {
            latch.countDown();
        }
        
        isAlreadyStarted.set(false);
        
        if(executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
	}

	@Override
	public String status() {
		return !isStop.get() ? "Publishing" : "Idle";
	}
	
	private class Publisher implements Runnable {
        public void run() {
            while(!isStop.get() && amount.get() > 0){
                long _speed = speed.get();
            	long sleepTime = timeUnitInNanos.get() / _speed;
                long timeLeft = timeUnitInNanos.get();
                long start = System.nanoTime();
                long index = 0;
                for(; index < _speed && timeLeft > 0; index++) {
                    try {
                        long delta = ((_speed - index) * sleepTime) - timeLeft;
                        latch.await(sleepTime - delta, TimeUnit.NANOSECONDS);

                        String msg = (String) message.get();
                        if (!Strings.isNullOrEmpty(replaceWithTime)) {
                        	String now = sdf.format(new Date());
                        	msg = msg.replaceAll(replaceWithTime, now);
                        }
                        
                        client.addMessage(index + "1", msg , null);
                        client.flush();
                        stats.incrementAndGetSent();

                        if (isStop.get() || amount.decrementAndGet() <= 0){
                            isStop.set(true);
                            log.info("setStop true amountToPublish: " + amount.get());
                            break;
                        }
                    } catch (Exception e) {
                    	stats.incrementAndGetExceptions();
                    	log.error("Exception: ", e);
                    }
                    
                    timeLeft = timeUnitInNanos.get() - (System.nanoTime() - start);
                }

				int acks = successPerTimeUnitCount.get();
				log.info(String.format("Published %d messages, and got %d ACCEPTED acks in %d nanoseconds (%s).",
                		index, acks, System.nanoTime() - start, timeUnitStr));
				if(acks == 0){
					log.info("No acks received for last batch.. something is wrong.. stopping publishing");
					stop();
				}
                successPerTimeUnitCount.set(0);
            }
            
            isStop.set(true);
        }
    }
	
	@Scheduled(fixedRate=300000) // every 5 minutes
	public void checkReconnect() throws InterruptedException, EventHubClientException {
		log.info("is client closed: {}", isClientClosed.get());
		if (isClientClosed.get()) {
			Thread.sleep(30000);
			startWithLocalDetails();
			int rec = stats.incrementAndGetReconnects();
			log.warn("Reconnected {} times.", rec);
		}
	}
}
