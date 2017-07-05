package com.ge.predix.cyber.csam.ehsender.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.ge.predix.cyber.csam.ehsender.entities.EventHubConnection;

/**
 * Created by Martin Saad on 2/8/2017.
 */
@Configuration
public class EventHubConfig {

    @Value("${P_EVENT_HUB_HOST}")
    private String pEventhubHost;

    @Value("${P_EVENT_HUB_PORT}")
    private Integer pEventhubPort;

    @Value("${P_EVENT_HUB_ZONE_ID}")
    private String pZoneId;

    @Value("${P_EVENT_HUB_UAA_URL}")
    private String pUaaUrl;

    @Value("${P_EVENT_HUB_UAA_CLIENT_ID}")
    private String pUaaClientId;

    @Value("${P_EVENT_HUB_UAA_CLIENT_SECRET}")
    private String pClientSecret;

    @Bean
    @Scope("prototype")
    public EventHubConnection eventHubPublisherConnection() {
        return new EventHubConnection(pUaaUrl, pUaaClientId, pClientSecret, pEventhubHost, pEventhubPort, pZoneId);
    }
}
