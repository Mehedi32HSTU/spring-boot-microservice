package com.javabean.order_service.sample.order;

import org.springframework.http.ResponseEntity;

public interface OrderService {
    public ResponseEntity<?> getOrderDetailsById(Long orderId);
    public ResponseEntity<?> getAllOrderDetails();
}
