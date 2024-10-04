package com.javabean.customer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@SpringBootApplication
@RestController("/")
public class CustomerServiceApplication {
	public static long appStartTime;
	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
		appStartTime = System.currentTimeMillis();
	}

	@GetMapping()
	public String getApplicationInfo() {
		return "Hello, CUSTOMER-SERVICE APPLICATION IS RUNNING SINCE : " +
				new Date(CustomerServiceApplication.appStartTime);
	}
}
