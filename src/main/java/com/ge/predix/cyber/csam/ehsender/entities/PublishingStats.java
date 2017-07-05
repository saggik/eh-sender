package com.ge.predix.cyber.csam.ehsender.entities;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

@Component
public class PublishingStats {
	private AtomicInteger sentCount = new AtomicInteger(0);
    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger failureCount = new AtomicInteger(0);
    private AtomicInteger exceptionCount = new AtomicInteger(0);
    private AtomicInteger reconnectCount = new AtomicInteger(0);
	
    public AtomicInteger getSentCount() {
		return sentCount;
	}
    
	public AtomicInteger getSuccessCount() {
		return successCount;
	}
	
	public AtomicInteger getFailureCount() {
		return failureCount;
	}
	
	public AtomicInteger getExceptionCount() {
		return exceptionCount;
	}

	public AtomicInteger getReconnectCount() {
		return reconnectCount;
	}
}
