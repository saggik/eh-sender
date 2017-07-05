package com.ge.predix.cyber.csam.ehsender.entities;

public enum TimeUnit {
	SECOND(1000000000l),
	MINUTE(60 * 1000000000l);
	
	private long toNano;
	
	TimeUnit(long toNano) {
		this.toNano = toNano;
	}
	
	public long toNano() {
		return toNano;
	}
}
