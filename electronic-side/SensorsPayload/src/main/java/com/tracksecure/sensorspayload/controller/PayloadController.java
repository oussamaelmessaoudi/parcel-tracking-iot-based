package com.tracksecure.sensorspayload.controller;


import com.tracksecure.sensorspayload.model.Payload;
import com.tracksecure.sensorspayload.service.PayloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;

@RestController
@RequestMapping("/sensor")
@RequiredArgsConstructor
public class PayloadController {
    private final PayloadService payloadService;

    @GetMapping("/data")
    public Payload getData(){
        return payloadService.getPayload();
    }
}
