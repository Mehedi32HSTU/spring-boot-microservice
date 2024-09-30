package com.javabean.product_service.sample.product;

import org.springframework.http.ResponseEntity;

public interface ProductService {
    public ResponseEntity<?> getProductDetailsById(Long productId);
    public ResponseEntity<?> getAllProductDetails();
}
