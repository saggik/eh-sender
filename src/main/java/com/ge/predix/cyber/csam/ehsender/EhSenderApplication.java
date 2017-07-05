package com.ge.predix.cyber.csam.ehsender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ge.predix.cyber.csam.ehsender.config.NettyConfig;

@EnableAutoConfiguration
@EnableScheduling
@ComponentScan(basePackages = "com.ge.predix.cyber.csam.ehsender")
@SpringBootApplication
public class EhSenderApplication {

	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		NettyConfig.getInstance().load();
		try {
			// wait for load application Vcap before spring initialized
			Thread.sleep( 5000 );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		SpringApplication.run(EhSenderApplication.class, args);
	}
}
