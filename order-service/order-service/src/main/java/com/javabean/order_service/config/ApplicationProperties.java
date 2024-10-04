package com.javabean.order_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
public class ApplicationProperties {
    @Value("${com.javabeans.customer.controller.url}")
    private String customerControllerUrl;

    @Value("${com.javabeans.product.controller.url}")
    private String productControllerUrl;

    public String getCustomerControllerUrl() {
        return customerControllerUrl;
    }

    public String getProductControllerUrl() {
        return productControllerUrl;
    }
}
