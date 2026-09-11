package com.khan.EComm.service;

import com.khan.EComm.dto.*;
import com.khan.EComm.model.Cart;
import com.khan.EComm.model.CartItem;
import com.khan.EComm.model.Product;
import com.khan.EComm.repo.CartItemRepository;
import com.khan.EComm.repo.CartRepository;
import com.khan.EComm.repo.ProductRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void addToCart(Long userId, AddToCartDTO request) {
        // 1. Validate Product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 2. Get or create cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        // 3. Check if item already exists
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if(cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(cartItem);
        }
        else {
            // create new cart item
            CartItem newItem = new CartItem(
                    cart.getId(),
                    product.getId(),
                    request.getQuantity()
            );
            cartItemRepository.save(newItem);
        }

    }

    @Transactional(readOnly = true)
    public CartResponseDTO getCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        List<CartItemResponseDTO> responseItems = new ArrayList<>();

        double totalAmount = 0.0;

        for (CartItem item : cartItems) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            CartItemResponseDTO responseItem = new CartItemResponseDTO(
                    product.getId(),
                    product.getName(),
                    product.getImageUrl(),
                    product.getPrice(),
                    item.getQuantity()
            );

            totalAmount += responseItem.getTotalPrice();
            responseItems.add(responseItem);
        }

        return new CartResponseDTO(responseItems, totalAmount);
    }

    @Transactional
    public void updateCartItem(Long userId, UpdateCartItemRequestDTO request) {

        // 1. Get cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // 2. Get cart item
        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        // 3. Handle quantity
        if (request.getQuantity() <= 0) {
            // remove item
            cartItemRepository.delete(cartItem);
        } else {
            // update quantity
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeCartItem(Long userId, RemoveCartItemRequestDTO request) {
        // 1. Get Cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        // 2. Get Cart item
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
            .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        // 3. Delete Cart item
        cartItemRepository.delete(cartItem);

    }
}
