package com.ge.predix.cyber.csam.ehsender.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.predix.cyber.csam.ehsender.entities.PublishingStats;

@Component
public class StatsServiceImpl implements StatsService {

	@Autowired
	private PublishingStats publishStats;
	
	@Override
	public String getStats() {
		return String.format("Sent = %d, Accepted = %d, Failed = %d, Exceptions = %d, Reconnects = %d",
				publishStats.getSentCount().get(), publishStats.getSuccessCount().get(), 
				publishStats.getFailureCount().get(), publishStats.getExceptionCount().get(),
				publishStats.getReconnectCount().get());
		
	}

	@Override
	public int incrementAndGetSent() {
		return publishStats.getSentCount().incrementAndGet();
	}

	@Override
	public int incrementAndGetAccepted() {
		return publishStats.getSuccessCount().incrementAndGet();
	}

	@Override
	public int incrementAndGetFailed() {
		return publishStats.getFailureCount().incrementAndGet();
	}

	@Override
	public int incrementAndGetExceptions() {
		return publishStats.getExceptionCount().incrementAndGet();
	}

	@Override
	public int incrementAndGetReconnects() {
		return publishStats.getReconnectCount().incrementAndGet();
	}
	
	@Override
	public void clearAllCounters() {
		publishStats.getSentCount().set(0);
		publishStats.getSuccessCount().set(0);
		publishStats.getFailureCount().set(0);
		publishStats.getExceptionCount().set(0);
		publishStats.getReconnectCount().set(0);
	}
}
