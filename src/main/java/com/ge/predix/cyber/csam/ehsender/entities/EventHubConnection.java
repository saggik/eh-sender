package com.ge.predix.cyber.csam.ehsender.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by 212593950 on 3/2/2017.
 */
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class EventHubConnection {

    private String authURL;
    private String clientId;
    private String clientSecret;
    private String host;
    private Integer port;
    private String eventhubZoneId;
}