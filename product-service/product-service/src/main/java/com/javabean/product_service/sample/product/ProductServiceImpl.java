package com.javabean.product_service.sample.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ResponseEntity<?> getProductDetailsById(Long productId) {
        try {
            logger.info("getProductDetailsById Method is Called");
            Optional<Product> product = getDummyProductData().stream()
                    .filter(productData -> Objects.equals(productData.getId(), productId)).findFirst();
            if(!product.isPresent()) {
                logger.info("Product Not Found for Id : "+productId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Product Not Found for Id : "+productId));
            }
            return ResponseEntity.status(HttpStatus.OK).body(product.get());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception "+e.getMessage() +" Has Occurred in getProductDetailsById Method");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Exception "+e.getMessage() +" Has Occurred"));
        }
    }

    @Override
    public ResponseEntity<?> getAllProductDetails() {
        try {
            logger.info("getAllProductDetails Method is Called");

            return ResponseEntity.status(HttpStatus.OK).body(getDummyProductData());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception "+e.getMessage() +" Has Occurred in getAllProductDetails Method");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Exception "+e.getMessage() +" Has Occurred"));
        }
    }

    private List<Product> getDummyProductData() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(1L, "Laptop", "Electronics", 1200.50, 15));
        products.add(new Product(2L, "Smartphone", "Electronics", 600.00, 30));
        products.add(new Product(3L, "Headphones", "Accessories", 150.75, 50));
        products.add(new Product(4L, "Monitor", "Electronics", 300.99, 20));
        products.add(new Product(5L, "Keyboard", "Accessories", 50.49, 100));
        products.add(new Product(6L, "Mouse", "Accessories", 25.30, 120));
        products.add(new Product(7L, "External Hard Drive", "Storage", 110.00, 25));
        products.add(new Product(8L, "Gaming Console", "Electronics", 450.00, 10));
        products.add(new Product(9L, "Smartwatch", "Wearables", 200.75, 40));
        products.add(new Product(10L, "Tablet", "Electronics", 350.99, 35));

        return products;
    }


}
