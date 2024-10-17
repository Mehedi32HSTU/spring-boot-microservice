package com.javabean.api_gateway.api_key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyFilter extends AbstractGatewayFilterFactory<ApiKeyFilter.Config> {
    public static final String API_KEY_HEADER="API-Key";

    @Autowired
    private ApiKeyService apiKeyService;

    public ApiKeyFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String apiKey = exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);

            if (apiKey == null) {
                System.out.println("API Key Not Found");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String serviceId = extractServiceIdFromPath(exchange.getRequest().getURI().getPath());

            if (serviceId == null) {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return exchange.getResponse().setComplete();
            }

            String expectedApiKey = apiKeyService.getApiKey(serviceId);

            if (!apiKey.equals(expectedApiKey)) {
                System.out.println("Expected : "+expectedApiKey+", Found : "+apiKey);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
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
}
