package com.ge.predix.cyber.csam.ehsender.services;

public interface StatsService {
	String getStats();
	int incrementAndGetSent();
	int incrementAndGetAccepted();
	int incrementAndGetFailed();
	int incrementAndGetExceptions();
	int incrementAndGetReconnects();
	void clearAllCounters();
}
