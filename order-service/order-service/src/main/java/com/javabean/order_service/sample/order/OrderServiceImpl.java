package com.javabean.order_service.sample.order;

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
public class OrderServiceImpl implements OrderService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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


    private List<Order> getDummyOrderData() {
        List<Order> orders = new ArrayList<>();

        orders.add(new Order(1L, "Laptop", 2, 1200.50));
        orders.add(new Order(2L, "Smartphone", 5, 600.00));
        orders.add(new Order(3L, "Headphones", 3, 150.75));
        orders.add(new Order(4L, "Monitor", 1, 300.99));
        orders.add(new Order(5L, "Keyboard", 4, 50.49));
        orders.add(new Order(6L, "Mouse", 6, 25.30));
        orders.add(new Order(7L, "External Hard Drive", 2, 110.00));
        orders.add(new Order(8L, "Gaming Console", 1, 450.00));
        orders.add(new Order(9L, "Smartwatch", 3, 200.75));
        orders.add(new Order(10L, "Tablet", 4, 350.99));

        return orders;
    }

}
