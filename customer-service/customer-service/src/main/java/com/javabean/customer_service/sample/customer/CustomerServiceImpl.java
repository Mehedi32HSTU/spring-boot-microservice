package com.javabean.customer_service.sample.customer;

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
public class CustomerServiceImpl implements CustomerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ResponseEntity<?> getCustomerDetailsById(Long customerId) {
        try {
            logger.info("getCustomerDetailsById Method is Called");
            Optional<Customer> customer = getDummyCustomerData().stream()
                    .filter(customerData -> Objects.equals(customerData.getId(), customerId)).findFirst();
            if(!customer.isPresent()) {
                logger.info("Customer Not Found for Id : "+customerId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Customer Not Found for Id : "+customerId));
            }
            return ResponseEntity.status(HttpStatus.OK).body(customer.get());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception "+e.getMessage() +" Has Occurred in getCustomerDetailsById Method");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Exception "+e.getMessage() +" Has Occurred"));
        }
    }

    @Override
    public ResponseEntity<?> getAllCustomerDetails() {
        try {
            logger.info("getAllCustomerDetails Method is Called");

            return ResponseEntity.status(HttpStatus.OK).body(getDummyCustomerData());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception "+e.getMessage() +" Has Occurred in getAllCustomerDetails Method");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Exception "+e.getMessage() +" Has Occurred"));
        }
    }

    private List<Customer> getDummyCustomerData() {
        List<Customer> customers = new ArrayList<>();

        customers.add(new Customer(1L, "John", "Doe", "john.doe@example.com", "123-456-7890"));
        customers.add(new Customer(2L, "Jane", "Smith", "jane.smith@example.com", "987-654-3210"));
        customers.add(new Customer(3L, "Emily", "Johnson", "emily.johnson@example.com", "555-666-7777"));
        customers.add(new Customer(4L, "Michael", "Brown", "michael.brown@example.com", "444-333-2222"));
        customers.add(new Customer(5L, "Sarah", "Williams", "sarah.williams@example.com", "111-222-3333"));
        customers.add(new Customer(6L, "David", "Jones", "david.jones@example.com", "666-777-8888"));
        customers.add(new Customer(7L, "Laura", "Garcia", "laura.garcia@example.com", "999-888-7777"));
        customers.add(new Customer(8L, "Robert", "Martinez", "robert.martinez@example.com", "222-333-4444"));
        customers.add(new Customer(9L, "Sophia", "Lee", "sophia.lee@example.com", "333-444-5555"));
        customers.add(new Customer(10L, "James", "Clark", "james.clark@example.com", "888-999-1111"));

        return customers;
    }
}
