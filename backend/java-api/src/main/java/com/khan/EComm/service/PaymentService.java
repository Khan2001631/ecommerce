package com.khan.EComm.service;

import com.khan.EComm.model.PaymentOrder;
import com.khan.EComm.repo.PaymentRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    @Autowired
    PaymentRepo paymentRepo;

    @Autowired
    EmailService emailService;

    public String createOrder(PaymentOrder orderDetails) throws RazorpayException {
        RazorpayClient client = new RazorpayClient(keyId,keySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount",(int)(orderDetails.getAmount() *100));
        orderRequest.put("currency","INR");
        orderRequest.put("receipt","txn_"+ UUID.randomUUID());

        Order razorpayOrder = client.orders.create(orderRequest);
        orderDetails.setOrderId(razorpayOrder.get("id"));
        orderDetails.setStatus("CREATED");
        orderDetails.setCreatedAt(LocalDateTime.now());

        paymentRepo.save(orderDetails);

        updateOrderStatus(null, razorpayOrder.get("id"), "SUCCESS");

        return orderDetails.toString();

    }

    public void updateOrderStatus(String paymentId, String orderId, String status) {
        PaymentOrder order = paymentRepo.findByOrderId(orderId);
        order.setPaymentId(paymentId);
        order.setStatus(status);
        paymentRepo.save(order);
        if("SUCCESS".equalsIgnoreCase(status)) {
            emailService.sendEmail(order.getEmail(),order.getName(),order.getAmount());
        }
    }
}
