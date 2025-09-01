package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.mapper.OrderMapper;
import com.hamster.ecommerce.model.dto.OrderDTO;
import com.hamster.ecommerce.model.entity.Order;
import com.hamster.ecommerce.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderController orderController;

    @InjectMocks
    private AdminOrderController adminOrderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllPaged() {
        // given
        Page<Order> page = new PageImpl<>(List.of(new Order()));
        when(orderService.find(any())).thenReturn(page);
        when(orderMapper.entityToDTO(any(Order.class))).thenReturn(new OrderDTO());

        // when
        ResponseEntity<Page<OrderDTO>> response =
                adminOrderController.findAllPaged(PageRequest.of(0, 10));

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
        verify(orderService, times(1)).find(any());
        verify(orderMapper, times(1)).entityToDTO(any(Order.class));
    }

    @Test
    void testFindOne() {
        // given
        Order entity = new Order();
        when(orderService.findById(1L)).thenReturn(Optional.of(entity));
        when(orderMapper.entityToDTO(entity)).thenReturn(new OrderDTO());

        // when
        ResponseEntity<OrderDTO> response = adminOrderController.findOne(1L);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).findById(1L);
        verify(orderMapper, times(1)).entityToDTO(entity);
    }

    @Test
    void testFindOne_NotFound() {
        // given
        when(orderService.findById(999L)).thenReturn(Optional.empty());

        // when / then
        try {
            adminOrderController.findOne(999L);
        } catch (NotFoundException e) {
            // adjust if your NotFoundException message differs
            assertEquals("Item not found : 999", e.getMessage());
        }

        verify(orderService, times(1)).findById(999L);
        verify(orderMapper, never()).entityToDTO(any());
    }

    @Test
    void testFindMyOrders() {
        // given
        Page<Order> page = new PageImpl<>(List.of(new Order()));
        when(orderService.findMyOrders(any())).thenReturn(page);
        when(orderMapper.entityToDTO(any(Order.class))).thenReturn(new OrderDTO());

        // when
        ResponseEntity<Page<OrderDTO>> response =
                orderController.findMyOrders(PageRequest.of(0, 10));

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
        verify(orderService, times(1)).findMyOrders(any());
        verify(orderMapper, times(1)).entityToDTO(any(Order.class));
    }

    @Test
    void testCheckout() {
        // given
        Order entity = new Order();
        when(orderService.checkout()).thenReturn(entity);
        when(orderMapper.entityToDTO(entity)).thenReturn(new OrderDTO());

        // when
        ResponseEntity<OrderDTO> response = orderController.checkout();

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).checkout();
        verify(orderMapper, times(1)).entityToDTO(entity);
    }

    @Test
    void testUpdateStatus() {
        // given
        Long id = 7L;
        String status = "SHIPPED";
        Order entity = new Order();

        when(orderService.updateStatus(id, status)).thenReturn(entity);
        when(orderMapper.entityToDTO(entity)).thenReturn(new OrderDTO());

        // when
        ResponseEntity<OrderDTO> response = orderController.updateStatus(id, status);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService, times(1)).updateStatus(id, status);
        verify(orderMapper, times(1)).entityToDTO(entity);
    }
}