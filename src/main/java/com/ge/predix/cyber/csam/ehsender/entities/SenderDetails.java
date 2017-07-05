package com.ge.predix.cyber.csam.ehsender.entities;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel(value = "sender details", description = "message, speed and amount for the sender")
@Data
public class SenderDetails {
	
	@NotNull
	private String message;
	
	@Min(1)
	@Max(10000)
	private long speed;
	
	@NotNull
	private TimeUnit timeUnit;
	
	@Min(0)
	private long amount;
	
	private String replaceWithTime;
}
