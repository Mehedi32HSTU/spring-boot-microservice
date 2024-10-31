package com.javabean.order_service.sample.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javabean.order_service.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    @Lazy
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public ResponseEntity<?> getOrderDetailsById(Long orderId) {
        try {
            logger.info("getOrderDetailsById Method is Called");
            Optional<Order> order = getDummyOrderData().stream()
                    .filter(orderData -> Objects.equals(orderData.getId(), orderId)).findFirst();
            if(!order.isPresent()) {
                logger.info("Order Not Found for Id : "+orderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Order Not Found for Id : "+orderId));
            }
            return ResponseEntity.status(HttpStatus.OK).body(order.get());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception "+e.getMessage() +" Has Occurred in getOrderDetailsById Method");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Exception "+e.getMessage() +" Has Occurred"));
        }
    }

    @Override
    public ResponseEntity<?> getAllOrderDetails() {
        try {
            logger.info("getAllOrderDetails Method is Called");

            return ResponseEntity.status(HttpStatus.OK).body(getDummyOrderData());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception "+e.getMessage() +" Has Occurred in getAllOrderDetails Method");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Exception "+e.getMessage() +" Has Occurred"));
        }
    }

    @Override
    public ResponseEntity<?> createOrder(Order order) {
        try {
            logger.info("createOrder Method is Called");
            Customer customer = null;
            ResponseEntity<String> customerResponse = getCustomerDetails(order.getCustomerId());
            if (customerResponse.getStatusCode().is2xxSuccessful()) {
                customer = objectMapper.readValue(customerResponse.getBody(), Customer.class);
            } else {
                String errorMessage = (String) customerResponse.getBody();
                logger.error("Failed to retrieve customer details. Status code: " + customerResponse.getStatusCode() + ", Message: " + errorMessage);
                return ResponseEntity.status(customerResponse.getStatusCode())
                        .body(new MessageResponse(errorMessage));
            }
            if(Objects.isNull(customer))
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Customer Not Found for Id : " + order.getCustomerId()));

            Product product = null;
            ResponseEntity<String> productResponse = getProductDetails(order.getProductName());
            if (productResponse.getStatusCode().is2xxSuccessful()) {
                product = objectMapper.readValue(productResponse.getBody(), Product.class); // Cast to Customer
            } else {
                String errorMessage = (String) productResponse.getBody();
                logger.error("Failed to retrieve product details. Status code: " + productResponse.getStatusCode() + ", Message: " + errorMessage);
                return ResponseEntity.status(productResponse.getStatusCode())
                        .body(new MessageResponse(errorMessage));
            }
            if(Objects.isNull(product))
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Product Not Found for Name : " + order.getProductName()));

            return ResponseEntity.status(HttpStatus.OK).body(order);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception "+e.getMessage() +" Has Occurred in createOrder Method");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Exception "+e.getMessage() +" Has Occurred"));
        }
    }

    private ResponseEntity<String> getProductDetails(String productName) {
        try {
            String url = applicationProperties.getProductControllerUrl() + "?name=" + productName;
            String token = "eyJhbGciOiJIUzI1NiJ9." +
                    "eyJmaXJzdG5hbWUiOiJBZG1pbiIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJST0xFX0FETUlOIn1dLCJpZCI6MSwiZW1ha" +
                    "WwiOiJhZG1pbkBhZG1pbi5jb20iLCJsYXN0bmFtZSI6IkFkbWluIiwidXNlcm5hbWUiOiJkZWZhdWx0X2FkbWluIiwic3" +
                    "ViIjoiZGVmYXVsdF9hZG1pbiIsImlhdCI6MTcyOTM2MTQwNCwiZXhwIjoxNzI5NDQ3ODA0fQ." +
                    "lx266ijYTEXvcUCiKtaUJAONe3MWYn2cVSjxIaVlqLM";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token); // Example header
            headers.set("service-name", "PRODUCT-SERVICE"); // Add any other custom headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make the request using exchange method to capture the full response
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody()); // Returning the product details in response
        } catch (HttpClientErrorException e) {
            return getErrorResponse(e);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception " + e.getMessage() + " has occurred in getProductDetails method");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve product details.");
        }
    }
    private ResponseEntity<String> getCustomerDetails(Long customerId) {
        try {
            String url = applicationProperties.getCustomerControllerUrl() + "/" + customerId;
            String token = "eyJhbGciOiJIUzI1NiJ9." +
                    "eyJmaXJzdG5hbWUiOiJBZG1pbiIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJST0xFX0FETUlOIn1dLCJpZCI6MSwiZW1ha" +
                    "WwiOiJhZG1pbkBhZG1pbi5jb20iLCJsYXN0bmFtZSI6IkFkbWluIiwidXNlcm5hbWUiOiJkZWZhdWx0X2FkbWluIiwic3" +
                    "ViIjoiZGVmYXVsdF9hZG1pbiIsImlhdCI6MTcyOTM2MTQwNCwiZXhwIjoxNzI5NDQ3ODA0fQ." +
                    "lx266ijYTEXvcUCiKtaUJAONe3MWYn2cVSjxIaVlqLM";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token); // Example header
            headers.set("service-name", "CUSTOMER-SERVICE"); // Add any other custom headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make the request using exchange method to capture the full response
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody()); // Returning the customer details in response
        } catch (HttpClientErrorException e) {
            return getErrorResponse(e);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception " + e.getMessage() + " has occurred in getCustomerDetails method");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve customer details.");
        }
    }
    private ResponseEntity<String> getErrorResponse(HttpClientErrorException e) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> errorResponse = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
            String message = errorResponse.get("message"); // Extract the message
            return ResponseEntity.status(e.getStatusCode()).body(message);
        } catch (Exception parseException) {
            logger.error("Failed to parse error response: " + parseException.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }


    private List<Order> getDummyOrderData() {
        List<Order> orders = new ArrayList<>();

        orders.add(new Order(1L, "Laptop", 1L, 2, 1200.50));
        orders.add(new Order(2L, "Smartphone", 2L, 5, 600.00));
        orders.add(new Order(3L, "Headphones", 3L, 3, 150.75));
        orders.add(new Order(4L, "Monitor", 4L, 1, 300.99));
        orders.add(new Order(5L, "Keyboard", 5L, 4, 50.49));
        orders.add(new Order(6L, "Mouse", 6L, 6, 25.30));
        orders.add(new Order(7L, "External Hard Drive", 7L, 2, 110.00));
        orders.add(new Order(8L, "Gaming Console", 8L, 1, 450.00));
        orders.add(new Order(9L, "Smartwatch", 9L, 3, 200.75));
        orders.add(new Order(10L, "Tablet", 10L, 4, 350.99));

        return orders;
    }

}
