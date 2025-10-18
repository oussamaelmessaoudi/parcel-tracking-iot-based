package com.tracksecure.iotgatewayservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private final StringRedisTemplate stringRedisTemplate;
    private static final Duration TTL = Duration.ofMinutes(10); //(time to live) which means each key will automatically expires after 10 minutes

    public boolean isDuplicated(String key){
        return stringRedisTemplate.hasKey(key);
    }

    public void markProcessed(String key){
        stringRedisTemplate.opsForValue().set(key,"processed",TTL);
        //this comes after a successfully processing a request and method will mark it as "processed"
        // if the same request sent again (holding the same key, isDuplicated() will catch it
    }
}
