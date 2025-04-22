package com.khan.EComm.service;

import com.khan.EComm.dto.OrderDTO;
import com.khan.EComm.dto.OrderItemDTO;
import com.khan.EComm.model.OrderItem;
import com.khan.EComm.model.Orders;
import com.khan.EComm.model.Product;
import com.khan.EComm.model.User;
import com.khan.EComm.repo.OrderRepository;
import com.khan.EComm.repo.ProductRepository;
import com.khan.EComm.repo.UserRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public OrderDTO placeOrder(Long userId, Map<Long, Integer> productQuantities) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Orders order = new Orders();
        order.setUser(user);
        order.setStatus("Pending");
        order.setOrderDate(new Date());
        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItemDTO>  orderItemDTOS = new ArrayList<>();
        double calculatedTotal = 0.0;
        for(Map.Entry<Long,Integer> entry: productQuantities.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(()->new RuntimeException("Product not found"));
            calculatedTotal += product.getPrice() * entry.getValue();
            order.setTotalAmount(calculatedTotal);
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(entry.getValue());
            orderItems.add(orderItem);

            orderItemDTOS.add(new OrderItemDTO(product.getName(),product.getPrice(),entry.getValue()));
        }
        order.setOrderItems(orderItems);
        Orders saveOrder = orderRepository.save(order);
        return new OrderDTO(saveOrder.getId(),saveOrder.getTotalAmount(),
                saveOrder.getStatus(),saveOrder.getOrderDate(),orderItemDTOS
        );
    }

    public List<OrderDTO> getAllOrders() {
        List<Orders> orders = orderRepository.findAllOrdersWithUsers();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Orders orders) {
        List<OrderItemDTO> orderItemDTOS = orders.getOrderItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity()
                )).collect(Collectors.toList());
        return new OrderDTO(
                orders.getId(),
                orders.getTotalAmount(),
                orders.getStatus(),
                orders.getOrderDate(),
                orders.getUser() != null ? orders.getUser().getName() : "Unknown",
                orders.getUser() != null ? orders.getUser().getEmail() : "Unknown",
                orderItemDTOS
        );
    }


    public List<OrderDTO> getOrderByUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        List<Orders> orderList = orderRepository.findByUser(user);
        return orderList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
