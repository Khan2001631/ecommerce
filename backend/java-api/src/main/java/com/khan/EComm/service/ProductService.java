package com.khan.EComm.service;

import com.khan.EComm.events.EventPublisher;
import com.khan.EComm.events.ProductAddedEvent;
import com.khan.EComm.model.Product;
import com.khan.EComm.repo.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;

    public ProductService(ProductRepository productRepository, EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> searchProductsByName(String query) {
        return productRepository.findByNameContainingIgnoreCase((query));
    }

    public Product addProduct(Product product, Long adminId) {
        Product savedProduct = productRepository.save(product);
        eventPublisher.publish(
                new ProductAddedEvent(
                        savedProduct.getId(),
                        adminId
                )
        );
        return savedProduct;
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
