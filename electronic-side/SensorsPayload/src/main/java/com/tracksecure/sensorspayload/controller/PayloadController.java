package com.tracksecure.sensorspayload.controller;

public class PayloadController {
<<<<<<< Updated upstream
=======
    private final PayloadService payloadService;
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/data")
    // @GetMapping("/sensor/data")
    public Payload getData(){
        return payloadService.getPayload();
    }
    
>>>>>>> Stashed changes
}
