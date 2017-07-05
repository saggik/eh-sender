package com.ge.predix.cyber.csam.ehsender.services;

import com.ge.predix.cyber.csam.ehsender.entities.SenderDetails;
import com.ge.predix.eventhub.EventHubClientException;

public interface SenderService {

	String start(SenderDetails details) throws EventHubClientException;
	
	void stop();
	
	String status();
}
