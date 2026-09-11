package com.khan.EComm.controller;

import com.khan.EComm.dto.AddToCartDTO;
import com.khan.EComm.dto.CartResponseDTO;
import com.khan.EComm.dto.RemoveCartItemRequestDTO;
import com.khan.EComm.dto.UpdateCartItemRequestDTO;
import com.khan.EComm.service.CartService;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public String addToCart(@RequestBody AddToCartDTO request) {

        Logger logger = LoggerFactory.getLogger(this.getClass());

        logger.info("=== /cart/add CONTROLLER HIT ===");

        try {
            logger.info("Incoming request: {}", request);

            if (request == null) {
                logger.warn("Request body is NULL");
            } else {
                logger.info("Product ID: {}", request.getProductId());
                logger.info("Quantity: {}", request.getQuantity());
            }

        // ⚠️ Replace with actual auth extraction later
        Long userId = 1L;
        logger.info("Using userId: {}", userId);
        cartService.addToCart(userId, request);
        logger.info("Product successfully added to cart ✅");
        return "Product added to cart";
        } catch (Exception e) {
            logger.error("❌ Exception in /cart/add:", e);
            throw e; // important: rethrow so you see full stack trace
        }
    }

    @GetMapping
    public CartResponseDTO getCart() {

        // ⚠️ Replace with real auth later
        Long userId = 1L;

        return cartService.getCart(userId);
    }

    @PutMapping("/update")
    public String updateCartItem(@RequestBody UpdateCartItemRequestDTO request) {

        // ⚠️ Replace later with real auth
        Long userId = 1L;

        cartService.updateCartItem(userId, request);

        return "Cart updated successfully";
    }

    @DeleteMapping("/remove")
    public String removeCartItem(@RequestBody RemoveCartItemRequestDTO request) {

        // ⚠️ Replace with real auth later
        Long userId = 1L;

        cartService.removeCartItem(userId, request);

        return "Item removed from cart";
    }
}
