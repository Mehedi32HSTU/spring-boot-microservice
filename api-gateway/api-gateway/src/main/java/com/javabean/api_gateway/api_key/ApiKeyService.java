package com.javabean.api_gateway.api_key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    // Store the API key for a specific service in Redis
    public void storeApiKey(String serviceId, String apiKey) {
        redisTemplate.opsForValue().set(serviceId, apiKey);
    }

    // Retrieve the API key for a specific service from Redis
    public String getApiKey(String serviceId) {
        return redisTemplate.opsForValue().get(serviceId);
    }

}
