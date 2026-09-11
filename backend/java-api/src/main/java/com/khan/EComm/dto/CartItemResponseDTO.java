package com.khan.EComm.dto;

public class CartItemResponseDTO {

    private Long productId;
    private String name;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private Double totalPrice;

    // constructor
    public CartItemResponseDTO(Long productId, String name, String imageUrl,
                            Double price, Integer quantity) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = price * quantity;
    }

    // getters

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
