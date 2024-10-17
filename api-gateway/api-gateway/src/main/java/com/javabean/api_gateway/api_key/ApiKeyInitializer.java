package com.javabean.api_gateway.api_key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyInitializer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private ApiKeyProperties apiKeyProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeApiKeys() {
        // Loop through the API keys and store each one in Redis
        apiKeyProperties.getKeys().forEach((serviceId, apiKey) -> {
            apiKeyService.storeApiKey(serviceId, apiKey);
            logger.info("Service Id {}, API-KEY: {}", serviceId, apiKey);
        });
    }
}
