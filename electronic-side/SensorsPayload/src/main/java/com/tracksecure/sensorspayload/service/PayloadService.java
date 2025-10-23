package com.tracksecure.sensorspayload.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracksecure.sensorspayload.model.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PayloadService {
    private Payload latestPayload;

    public void processPayload(String json){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Payload payload = objectMapper.readValue(json, Payload.class);
            payload.setTimestamp(LocalDateTime.now());
            latestPayload = payload;
            log.info("Payload received "+payload);
        }catch(Exception e){
            log.error("Invalid payload "+e.getMessage());
        }
    }
    public Payload getPayload(){
        return latestPayload;
    }
}
