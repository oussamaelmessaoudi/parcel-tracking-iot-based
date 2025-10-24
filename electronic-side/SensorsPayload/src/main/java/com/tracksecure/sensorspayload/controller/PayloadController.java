package com.tracksecure.sensorspayload.controller;

import com.tracksecure.sensorspayload.model.Payload;
import com.tracksecure.sensorspayload.service.PayloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sensor")
@RequiredArgsConstructor
public class PayloadController {
    private final PayloadService payloadService;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/data")
    public Payload getData(){
        return payloadService.getPayload();
    }
}
