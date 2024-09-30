package com.javabean.customer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@SpringBootApplication
@RestController("/")
public class CustomerServiceApplication {
	public static long appStartTime;
	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
		appStartTime = System.currentTimeMillis();
	}
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@GetMapping()
	public String getApplicationInfo() {
		return "Hello, CUSTOMER-SERVICE APPLICATION IS RUNNING SINCE : " +
				new Date(CustomerServiceApplication.appStartTime);
	}
}
