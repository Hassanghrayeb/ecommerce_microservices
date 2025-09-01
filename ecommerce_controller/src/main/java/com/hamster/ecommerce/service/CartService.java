package com.hamster.ecommerce.service;

import com.hamster.ecommerce.model.entity.Cart;
import com.hamster.ecommerce.model.simple.AddToCartRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CartService
{
    Page<Cart> find(Pageable pageable);
    Optional<Cart> findByUserId(Long userId);
    Cart findMyCart();
    Cart saveCart(Cart cart);
    void clear();
    Cart removeItem(Long cartItemId);
    Cart updateItemQuantity(Long cartItemId, int quantity);
    Cart addItem(AddToCartRequest addToCartRequest);
    Cart getOrCreate(Long userId);
    void delete(Long id);
}
