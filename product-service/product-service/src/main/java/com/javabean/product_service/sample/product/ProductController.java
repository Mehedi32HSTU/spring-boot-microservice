package com.javabean.product_service.sample.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/controller")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    public ResponseEntity<?> getProductDetailsById(@PathVariable(name = "productId") Long productId) {
        return productService.getProductDetailsById(productId);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<?> getAllProductDetails() {
        return productService.getAllProductDetails();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getProductDetailsByName(@RequestParam(name = "name") String productName) {
        return productService.getProductDetailsByName(productName);
    }
}
