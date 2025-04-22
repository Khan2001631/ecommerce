package com.khan.EComm.controller;

import com.khan.EComm.dto.OrderDTO;
import com.khan.EComm.model.OrderRequest;
import com.khan.EComm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/place/{userId}")
    public OrderDTO placeOrder(@PathVariable Long userId, @RequestBody Map<Long,Integer> productQuantities) {
        System.out.println(productQuantities);
        return orderService.placeOrder(userId, productQuantities);
    }

    @GetMapping("/all-orders")
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/user/{userId}")
    public List<OrderDTO> getOrderByUser(@PathVariable Long userId) {
        return orderService.getOrderByUser(userId);
    }
}
