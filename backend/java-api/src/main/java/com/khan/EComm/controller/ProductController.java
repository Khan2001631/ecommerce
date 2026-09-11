package com.khan.EComm.controller;

import com.khan.EComm.model.Product;
import com.khan.EComm.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin("*")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String query) {
        return productService.searchProductsByName(query);
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        Long adminId = 1L; // NOTE: Call appropriately later
        return productService.addProduct(product, adminId);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
