package com.khan.EComm.repo;

import com.khan.EComm.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo  extends JpaRepository<PaymentOrder,Long> {
    PaymentOrder findByOrderId(String orderId);
}
