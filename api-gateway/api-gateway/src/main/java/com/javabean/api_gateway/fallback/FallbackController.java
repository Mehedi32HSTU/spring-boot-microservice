package com.javabean.api_gateway.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/customer")
    public ResponseEntity<?> customerServiceFallBack() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new FallbackResponse("Customer Service is taking too long to respond or is down. Please try again later"));
    }
    @RequestMapping("/order")
    public ResponseEntity<?> orderServiceFallBack() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new FallbackResponse("Order Service is taking too long to respond or is down. Please try again later"));
    }
    @RequestMapping("/product")
    public ResponseEntity<?> productServiceFallBack() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new FallbackResponse("Product Service is taking too long to respond or is down. Please try again later"));
    }
}
