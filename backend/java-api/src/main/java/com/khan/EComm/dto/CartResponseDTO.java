package com.khan.EComm.dto;
import java.util.List;

public class CartResponseDTO {

    private List<CartItemResponseDTO> items;
    private Double totalAmount;

    public CartResponseDTO(List<CartItemResponseDTO> items, Double totalAmount) {
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public List<CartItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponseDTO> items) {
        this.items = items;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
