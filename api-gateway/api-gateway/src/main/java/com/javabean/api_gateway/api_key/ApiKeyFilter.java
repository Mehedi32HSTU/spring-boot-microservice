package com.javabean.api_gateway.api_key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ApiKeyFilter extends AbstractGatewayFilterFactory<ApiKeyFilter.Config> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String API_KEY_HEADER="API-Key";

    @Autowired
    private ApiKeyService apiKeyService;

    public ApiKeyFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String encryptedApiKey = exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);

            if (encryptedApiKey == null) {
                logger.info("API Key Not Found.");
                return prepareErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                        "Unauthorized access. API Key Not Found.");
            }

            String serviceId = extractServiceIdFromPath(exchange.getRequest().getURI().getPath());

            if (serviceId == null) {
                logger.info("Service Name Not Found.");
                return prepareErrorResponse(exchange, HttpStatus.BAD_REQUEST,
                        "Invalid Request. Service Name Not Found.");
            }

            String decryptedApiKey = "";
            try {
                decryptedApiKey = EncryptionUtil.decrypt(encryptedApiKey);
            } catch (Exception e) {
                logger.info("Failed to decrypt the key.");
                return prepareErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                        "Unauthorized access. Invalid API Key.");
            }
            String expectedApiKey = apiKeyService.getApiKey(serviceId);

            if (!decryptedApiKey.equals(expectedApiKey)) {
                logger.info("API Key Not Matched.");
                return prepareErrorResponse(exchange, HttpStatus.UNAUTHORIZED,
                        "Unauthorized access. API Key does not match.");
            }

            return chain.filter(exchange);
        };
    }

    private String extractServiceIdFromPath(String path) {
        if (path != null) {
            String[] segments = path.split("/");
            if (segments.length > 1) {
                return segments[1];  // Extract the service ID from the path
            }
        }
        return null;
    }

    public static class Config {
        // You can put any additional configuration properties here if needed
    }
    private Mono<Void> prepareErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);

        String responseBody = String.format("{\"message\": \"%s\"}", message);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getResponse().writeWith(Mono.just(
                exchange.getResponse().bufferFactory().wrap(responseBody.getBytes())
        ));
    }
}
