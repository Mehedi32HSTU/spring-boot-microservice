package com.javabean.customer_service.sample.customer;

import org.springframework.http.ResponseEntity;

public interface CustomerService {
    public ResponseEntity<?> getCustomerDetailsById(Long customerId);
    public ResponseEntity<?> getAllCustomerDetails();
}
