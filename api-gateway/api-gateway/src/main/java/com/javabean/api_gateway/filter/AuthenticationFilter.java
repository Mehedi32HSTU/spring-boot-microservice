package com.javabean.api_gateway.filter;

import com.javabean.api_gateway.filter.service_name.ServiceNameProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String SERVICE_NAME_HEADER="service-name";
    public static final String SECURITY_SERVICE_URL = "lb://API-GATEWAY/gateway-security";
    public static final String TOKEN_VALIDATION_SUFFIX = "/auth/validate-token";


    @Autowired
    private ServiceNameProperties serviceNameProperties;

    /*@Autowired
    private WebClient webClient;*/
    @Autowired
    public WebClient.Builder webClientBuilder;

    public AuthenticationFilter() {
        super(Config.class);
    }

    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            // 1. Extract the Service Name
            String serviceName = getServiceName(exchange);
            if (Objects.isNull(serviceName) || !serviceNameProperties.getServiceNames().containsKey(serviceName)) {
                logger.info("Service Name Not Found.");
                return prepareErrorResponse(exchange, HttpStatus.BAD_REQUEST, "Invalid Request. Service Name Not Found.");
            }

            // 2. Check for Authorization Header (Token)
            String authHeader = getAuthorizationToken(exchange);
            if (Objects.isNull(authHeader)) {
                logger.info("Token Not Found.");
                return prepareErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Token Not Found.");
            }

            // 3. Prepare Token Validation Request
            String requestUrl = exchange.getRequest().getURI().getPath();
            TokenValidationRequestDTO requestDTO = buildTokenValidationRequest(authHeader, requestUrl, serviceName);

            // 4. Call the Token Validation Service
            return validateToken(requestDTO, TOKEN_VALIDATION_SUFFIX)
                    .flatMap(responseDTO -> {
                        // 5. Process the Token Validation Response
                        if (Objects.isNull(responseDTO) || !responseDTO.isValid()) {
                            return prepareErrorResponse(exchange, HttpStatus.UNAUTHORIZED, responseDTO.getMessage());
                        }
                        // Continue the filter chain
                        return chain.filter(exchange);
                    })
                    .onErrorResume(e -> {
                        logger.error("Error during token validation", e);
                        return prepareErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid Token.");
                    });
        });
    }
/*
    private String extractServiceNameFromPath(String path) {
        System.out.println("Path : "+path);
        if (path != null) {
            String[] segments = path.split("/");
            if (segments.length > 1) {
                return segments[1];  // Extract the service ID from the path and convert it to uppercase.
            }
        }
        return null;
    }
*/

    public static class Config { }
    private Mono<Void> prepareErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);

        String responseBody = String.format("{\"message\": \"%s\"}", message);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getResponse().writeWith(Mono.just(
                exchange.getResponse().bufferFactory().wrap(responseBody.getBytes())
        ));
    }
    private String getServiceName(ServerWebExchange exchange) {
        List<String> headerValues = exchange.getRequest().getHeaders().get(SERVICE_NAME_HEADER);
        return (headerValues != null && !headerValues.isEmpty()) ? headerValues.get(0) : null;
    }
    private String getAuthorizationToken(ServerWebExchange exchange) {
        List<String> authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        String authHeader = null;
        if (authHeaders != null && !authHeaders.isEmpty()) {
            authHeader = authHeaders.get(0);
            if (authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }
        }
        return authHeader;
    }
    private TokenValidationRequestDTO buildTokenValidationRequest(String token, String url, String serviceName) {
        TokenValidationRequestDTO requestDTO = new TokenValidationRequestDTO();
        requestDTO.setToken(token);
        requestDTO.setUrl(url);
        requestDTO.setServiceName(serviceName);
        requestDTO.setContextPath(serviceNameProperties.getServiceNames().get(serviceName));
        return requestDTO;
    }
    private Mono<TokenValidationResponseDTO> validateToken(TokenValidationRequestDTO requestDTO, String urlSuffix) {
        String url = SECURITY_SERVICE_URL + urlSuffix;

        return webClientBuilder.build()
                .post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(TokenValidationResponseDTO.class);
    }

}
