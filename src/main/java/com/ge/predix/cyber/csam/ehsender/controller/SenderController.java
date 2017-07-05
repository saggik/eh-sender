package com.ge.predix.cyber.csam.ehsender.controller;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ge.predix.cyber.csam.ehsender.entities.MemoryMonitor;
import com.ge.predix.cyber.csam.ehsender.entities.SenderDetails;
import com.ge.predix.cyber.csam.ehsender.services.SenderService;
import com.ge.predix.cyber.csam.ehsender.services.StatsService;
import com.ge.predix.eventhub.EventHubClientException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "Sender Controller", description = "send endpoint")
public class SenderController {

    @Autowired
    private SenderService sender;
    
    @Autowired
    private StatsService stats;
    
    @Autowired
    private MemoryMonitor memory;

    @ResponseBody
    @ApiOperation(value = "start sending with details")
    @PostMapping(value = "/sender/start")
    public ResponseEntity<?> startSending(@Valid @RequestBody SenderDetails senderDetails) throws EventHubClientException {
    	sender.start(senderDetails);
    	return ResponseEntity.status(HttpStatus.OK).build();
    }
    
    @ResponseBody
    @ApiOperation(value = "stop sending")
    @PutMapping(value = "/sender/stop")
    public ResponseEntity<?> stopSending(){
    	sender.stop();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ResponseBody
    @ApiOperation(value = "get sender status")
    @GetMapping(value = "/sender/status")
    public ResponseEntity<String> getStatus(){
    	String status = sender.status();
        return ResponseEntity.status(HttpStatus.OK).body(status);
    }

    @ResponseBody
    @ApiOperation(value = "get all counters")
    @GetMapping(value = "/stats/all")
    public ResponseEntity<String> getAllCounters(){
        return ResponseEntity.status(HttpStatus.OK).body(stats.getStats());
    }
    
    @ResponseBody
    @ApiOperation(value = "get direct memory usage")
    @GetMapping(value = "/direct-memory")
    public ResponseEntity<String> getDirectMemoryUsage() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	String usage = memory.getDirectMemoryUsageOfMax();
        return ResponseEntity.status(HttpStatus.OK).body(usage);
    }
}