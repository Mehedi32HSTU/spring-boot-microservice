package com.javabean.order_service.sample.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String category;
    private double price;
    private int stock;
}
