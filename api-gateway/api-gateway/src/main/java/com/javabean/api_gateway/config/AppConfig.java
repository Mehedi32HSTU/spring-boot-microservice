package com.javabean.api_gateway.config;

import io.netty.channel.ChannelOption;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000); // Connection timeout (5 seconds)
        requestFactory.setReadTimeout(5000); // Read timeout (5 seconds)

        return new RestTemplate(requestFactory);
    }

    /*@Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }*/

    @Bean
    @LoadBalanced // Add this annotation to enable load balancing
    public WebClient.Builder webClientBuilder() {
        // Create a custom HttpClient with timeouts
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5)) // Response timeout
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000); // Connection timeout

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient)); // Use the custom HttpClient
    }

}
