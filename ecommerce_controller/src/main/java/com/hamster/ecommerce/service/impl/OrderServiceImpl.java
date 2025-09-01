package com.hamster.ecommerce.service.impl;

import com.hamster.ecommerce.model.entity.Cart;
import com.hamster.ecommerce.model.entity.CartItem;
import com.hamster.ecommerce.model.entity.Order;
import com.hamster.ecommerce.model.entity.OrderItem;
import com.hamster.ecommerce.repository.CartItemRepository;
import com.hamster.ecommerce.repository.CartRepository;
import com.hamster.ecommerce.repository.OrderItemRepository;
import com.hamster.ecommerce.repository.OrderRepository;
import com.hamster.ecommerce.service.OrderService;
import com.hamster.ecommerce.util.AuditUtil;
import com.hamster.ecommerce.util.ContextUtil;
import com.hamster.ecommerce.util.PriceUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService
{
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ContextUtil contextUtil;
    private final AuditUtil auditUtil;


    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository, CartItemRepository cartItemRepository, OrderItemRepository orderItemRepository, ContextUtil contextUtil, AuditUtil auditUtil)
    {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.contextUtil = contextUtil;
        this.auditUtil = auditUtil;
    }

    @Override
    public Page<Order> find(Pageable pageable)
    {
        Page<Order> orderPage =  orderRepository.findAll(pageable);
        return orderPage.map(this::populateOrder);
    }

    private Order populateOrder(Order order)
    {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        order.setItems(items);
        return order;
    }

    @Override
    public Optional<Order> findById(Long id)
    {
        return orderRepository.findById(id).map(this::populateOrder);
    }

    @Override
    public Page<Order> findByUserId(Long userId, Pageable pageable)
    {
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    public Page<Order> findMyOrders(Pageable pageable)
    {
        Long userId = contextUtil.getCurrentUserId();
        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);
    }


    @Override
    @Transactional("postgresTransactionManager")
    public Order checkout()
    {
        Long userId = contextUtil.getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Cart not found"));
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        if (items.isEmpty()) throw new IllegalStateException("Cart is empty");

        String currency = items.get(0).getCurrency();
        if (currency == null) throw new IllegalStateException("Currency missing");
        if (items.stream().anyMatch(i -> !currency.equals(i.getCurrency())))
            throw new IllegalStateException("Mixed currencies not supported");

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("NEW");
        order.setCurrency(currency);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotal(PriceUtil.scale(BigDecimal.ZERO, currency));
        auditUtil.setAuditColumns(order);

        order = orderRepository.save(order);

        BigDecimal total = PriceUtil.scale(BigDecimal.ZERO, currency);
        for (CartItem ci : items) {
            BigDecimal unit = PriceUtil.scale(ci.getUnitPrice(), currency);
            BigDecimal line = PriceUtil.multiply(unit, ci.getQuantity(), currency);
            total = PriceUtil.add(total, line, currency);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(ci.getProductId());
            orderItem.setProductName(ci.getProductName());
            orderItem.setUnitPrice(unit);
            orderItem.setQuantity(ci.getQuantity());
            orderItem.setLineTotal(line);
            orderItem.setCurrency(currency);
            auditUtil.setAuditColumns(orderItem);

            orderItemRepository.save(orderItem);
        }
        order.setTotal(total);
        orderRepository.save(order);

        cartItemRepository.deleteAllForCartId(cart.getId());
        order.setItems(orderItemRepository.findByOrderId(order.getId()));
        return order;
    }

    @Override
    @Transactional("postgresTransactionManager")
    public Order updateStatus(Long orderId, String status)
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with Id" + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
