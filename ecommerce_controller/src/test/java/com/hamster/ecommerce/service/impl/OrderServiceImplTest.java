package com.hamster.ecommerce.service.impl;

import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.model.entity.Cart;
import com.hamster.ecommerce.model.entity.CartItem;
import com.hamster.ecommerce.model.entity.Order;
import com.hamster.ecommerce.model.entity.OrderItem;
import com.hamster.ecommerce.repository.CartItemRepository;
import com.hamster.ecommerce.repository.CartRepository;
import com.hamster.ecommerce.repository.OrderItemRepository;
import com.hamster.ecommerce.repository.OrderRepository;
import com.hamster.ecommerce.util.AuditUtil;
import com.hamster.ecommerce.util.ContextUtil;
import com.hamster.ecommerce.util.PriceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.MockedStatic;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private ContextUtil contextUtil;
    @Mock private AuditUtil auditUtil;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -------- find() maps and populates items --------
    @Test
    void testFind_populatesItems() {
        Order o = new Order();
        o.setId(1L);

        Page<Order> page = new PageImpl<>(List.of(o));
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(List.of(new OrderItem()));

        Page<Order> result = orderService.find(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().get(0).getItems().size());
        verify(orderRepository).findAll(any(Pageable.class));
        verify(orderItemRepository).findByOrderId(1L);
    }

    // -------- findById() found + populates --------
    @Test
    void testFindById_found() {
        Order o = new Order();
        o.setId(2L);
        when(orderRepository.findById(2L)).thenReturn(Optional.of(o));
        when(orderItemRepository.findByOrderId(2L)).thenReturn(List.of(new OrderItem()));

        Optional<Order> res = orderService.findById(2L);

        assertTrue(res.isPresent());
        assertEquals(1, res.get().getItems().size());
        verify(orderRepository).findById(2L);
        verify(orderItemRepository).findByOrderId(2L);
    }

    @Test
    void testFindById_missing() {
        when(orderRepository.findById(9L)).thenReturn(Optional.empty());

        Optional<Order> res = orderService.findById(9L);

        assertTrue(res.isEmpty());
        verify(orderRepository).findById(9L);
        verify(orderItemRepository, never()).findByOrderId(anyLong());
    }

    // -------- findByUserId() & findMyOrders() delegate --------
    @Test
    void testFindByUserId_delegatesToRepo() {
        when(orderRepository.findAllByUserIdOrderByCreatedAtDesc(eq(7L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Order())));
        Page<Order> res = orderService.findByUserId(7L, PageRequest.of(0,10));
        assertEquals(1, res.getTotalElements());
        verify(orderRepository).findAllByUserIdOrderByCreatedAtDesc(eq(7L), any(Pageable.class));
    }

    @Test
    void testFindMyOrders_usesCurrentUser() {
        when(contextUtil.getCurrentUserId()).thenReturn(33L);
        when(orderRepository.findAllByUserIdOrderByCreatedAtDesc(eq(33L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Order())));

        Page<Order> res = orderService.findMyOrders(PageRequest.of(0, 10));

        assertEquals(1, res.getTotalElements());
        verify(orderRepository).findAllByUserIdOrderByCreatedAtDesc(eq(33L), any(Pageable.class));
    }

    // -------- checkout() happy path --------
    @Test
    void testCheckout_happyPath_buildsOrderTotals_andClearsCart() {
        Long userId = 5L;
        Long cartId = 100L;
        String currency = "USD";

        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setUserId(userId);

        CartItem i1 = new CartItem();
        i1.setCartId(cartId);
        i1.setProductId(10L);
        i1.setProductName("Mouse");
        i1.setUnitPrice(new BigDecimal("10.125"));
        i1.setCurrency(currency);
        i1.setQuantity(2);

        CartItem i2 = new CartItem();
        i2.setCartId(cartId);
        i2.setProductId(11L);
        i2.setProductName("Pad");
        i2.setUnitPrice(new BigDecimal("5.005"));
        i2.setCurrency(currency);
        i2.setQuantity(3);

        when(contextUtil.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(cartId)).thenReturn(List.of(i1, i2));

        // First save assigns order id; second save returns updated order
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            if (o.getId() == null) o.setId(777L);
            return o;
        });

        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderItemRepository.findByOrderId(777L)).thenReturn(new ArrayList<>()); // filled after save

        try (MockedStatic<PriceUtil> mockPrice = mockStatic(PriceUtil.class)) {
            // scale(0) calls
            mockPrice.when(() -> PriceUtil.scale(new BigDecimal("0"), currency))
                    .thenReturn(new BigDecimal("0.00"));

            // scale unit prices
            mockPrice.when(() -> PriceUtil.scale(new BigDecimal("10.125"), currency))
                    .thenReturn(new BigDecimal("10.13"));
            mockPrice.when(() -> PriceUtil.scale(new BigDecimal("5.005"), currency))
                    .thenReturn(new BigDecimal("5.01"));

            // line totals
            mockPrice.when(() -> PriceUtil.multiply(new BigDecimal("10.13"), 2, currency))
                    .thenReturn(new BigDecimal("20.26"));
            mockPrice.when(() -> PriceUtil.multiply(new BigDecimal("5.01"), 3, currency))
                    .thenReturn(new BigDecimal("15.03"));

            // running total: 0 + 20.26 + 15.03 = 35.29
            mockPrice.when(() -> PriceUtil.add(new BigDecimal("0.00"), new BigDecimal("20.26"), currency))
                    .thenReturn(new BigDecimal("20.26"));
            mockPrice.when(() -> PriceUtil.add(new BigDecimal("20.26"), new BigDecimal("15.03"), currency))
                    .thenReturn(new BigDecimal("35.29"));

            Order result = orderService.checkout();

            assertNotNull(result);
            assertEquals(777L, result.getId());
            assertEquals(currency, result.getCurrency());
            assertEquals(new BigDecimal("35.29"), result.getTotal());

            verify(auditUtil, atLeastOnce()).setAuditColumns(any()); // order + items
            verify(orderRepository, times(2)).save(any(Order.class)); // initial + after total
            verify(orderItemRepository, times(2)).save(any(OrderItem.class));
            verify(cartItemRepository, times(1)).deleteAllForCartId(cartId);
            verify(orderItemRepository, times(1)).findByOrderId(777L);

            mockPrice.verify(() -> PriceUtil.scale(new BigDecimal("0"), currency), times(2));
            mockPrice.verify(() -> PriceUtil.scale(new BigDecimal("10.125"), currency), times(1));
            mockPrice.verify(() -> PriceUtil.scale(new BigDecimal("5.005"), currency), times(1));
            mockPrice.verify(() -> PriceUtil.multiply(new BigDecimal("10.13"), 2, currency), times(1));
            mockPrice.verify(() -> PriceUtil.multiply(new BigDecimal("5.01"), 3, currency), times(1));
        }
    }

    // -------- checkout() error cases --------
    @Test
    void testCheckout_cartMissing_illegalState() {
        when(contextUtil.getCurrentUserId()).thenReturn(1L);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        IllegalStateException ex =
                assertThrows(IllegalStateException.class, () -> orderService.checkout());
        assertTrue(ex.getMessage().contains("Cart not found"));
    }

    @Test
    void testCheckout_emptyCart_illegalState() {
        Long userId = 1L;
        Cart cart = new Cart(); cart.setId(10L);
        when(contextUtil.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(10L)).thenReturn(Collections.emptyList());

        IllegalStateException ex =
                assertThrows(IllegalStateException.class, () -> orderService.checkout());
        assertTrue(ex.getMessage().contains("Cart is empty"));
    }

    @Test
    void testCheckout_currencyMissing_illegalState() {
        Long userId = 1L;
        Cart cart = new Cart(); cart.setId(10L);
        CartItem ci = new CartItem(); ci.setCurrency(null);

        when(contextUtil.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(10L)).thenReturn(List.of(ci));

        IllegalStateException ex =
                assertThrows(IllegalStateException.class, () -> orderService.checkout());
        assertTrue(ex.getMessage().contains("Currency missing"));
    }

    @Test
    void testCheckout_mixedCurrencies_illegalState() {
        Long userId = 1L;
        Cart cart = new Cart(); cart.setId(10L);
        CartItem a = new CartItem(); a.setCurrency("USD");
        CartItem b = new CartItem(); b.setCurrency("EUR");

        when(contextUtil.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(10L)).thenReturn(List.of(a, b));

        IllegalStateException ex =
                assertThrows(IllegalStateException.class, () -> orderService.checkout());
        assertTrue(ex.getMessage().contains("Mixed currencies not supported"));
    }

    // -------- updateStatus --------
    @Test
    void testUpdateStatus_updatesAndSaves() {
        Order o = new Order();
        o.setId(9L);
        o.setStatus("NEW");

        when(orderRepository.findById(9L)).thenReturn(Optional.of(o));
        when(orderRepository.save(o)).thenReturn(o);

        Order res = orderService.updateStatus(9L, "SHIPPED");

        assertEquals("SHIPPED", res.getStatus());
        verify(orderRepository).findById(9L);
        verify(orderRepository).save(o);
    }

    @Test
    void testUpdateStatus_missingOrder_throws() {
        when(orderRepository.findById(123L)).thenReturn(Optional.empty());

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> orderService.updateStatus(123L, "CANCELLED"));
        assertTrue(ex.getMessage().contains("Order not found with Id123"));
        verify(orderRepository, never()).save(any(Order.class));
    }
}