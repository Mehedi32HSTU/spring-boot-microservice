package com.javabean.product_service.sample.product;

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
