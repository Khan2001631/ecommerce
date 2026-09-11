package com.khan.EComm.repo;

import com.khan.EComm.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    List<CartItem> findByCartId(Long cartId);
}
