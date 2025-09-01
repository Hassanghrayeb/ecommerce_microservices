package com.hamster.ecommerce.service;

import com.hamster.ecommerce.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface OrderService
{
    Page<Order> find(Pageable pageable);
    Optional<Order> findById(Long id);
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findMyOrders(Pageable pageable);
    Order checkout();
    Order updateStatus(Long orderId, String status);
}
