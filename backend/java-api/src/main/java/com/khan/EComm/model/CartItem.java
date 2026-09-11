package com.khan.EComm.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_item", indexes = {
        @Index(name = "idx_cart_id", columnList = "cart_id"),
        @Index(name = "idx_product_id", columnList = "product_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_cart_product", columnNames = {"cart_id", "product_id"})
})
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cart_id", nullable = false)
    private Long cartId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    // Constructors
    public CartItem() {}

    public CartItem(Long cartId, Long productId, Integer quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
