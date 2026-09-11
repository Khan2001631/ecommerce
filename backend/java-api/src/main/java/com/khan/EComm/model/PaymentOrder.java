package com.khan.EComm.model;

import jakarta.persistence.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;

@Entity
@Table(name="payment_order")
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private double amount;
    private String orderId;
    private String paymentId;
    private String status;
    private LocalDateTime createdAt;

    public PaymentOrder() {}

    public PaymentOrder(LocalDateTime createdAt, String status, String paymentId, String orderId, double amount, String phone, String email, String name, Long id) {
        this.createdAt = createdAt;
        this.status = status;
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.phone = phone;
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
