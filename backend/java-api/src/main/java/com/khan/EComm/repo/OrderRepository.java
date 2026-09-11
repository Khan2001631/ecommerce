package com.khan.EComm.repo;

import com.khan.EComm.model.Orders;
import com.khan.EComm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders,Long> {
    @Query("Select o from Orders o JOIN FETCH o.user")
    List<Orders> findAllOrdersWithUsers();

    List<Orders>     findByUser(User user);
}
