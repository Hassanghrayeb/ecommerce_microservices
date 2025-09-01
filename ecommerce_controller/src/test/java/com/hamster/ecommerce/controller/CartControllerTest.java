package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.mapper.CartMapper;
import com.hamster.ecommerce.model.dto.CartDTO;
import com.hamster.ecommerce.model.entity.Cart;
import com.hamster.ecommerce.model.simple.AddToCartRequest;
import com.hamster.ecommerce.model.simple.UpdateCartItemRequest;
import com.hamster.ecommerce.service.CartService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartController cartController;

    @InjectMocks
    private AdminCartController adminCartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllPaged() {
        // given
        Page<Cart> carts = new PageImpl<>(List.of(new Cart()));
        when(cartService.find(any())).thenReturn(carts);
        when(cartMapper.entityToDTO(any(Cart.class))).thenReturn(new CartDTO());

        // when
        ResponseEntity<Page<CartDTO>> response =
                adminCartController.findAllPaged(PageRequest.of(0, 10));

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
        verify(cartService, times(1)).find(any());
        verify(cartMapper, times(1)).entityToDTO(any(Cart.class));
    }

    @Test
    void testAdd() {
        // given
        AddToCartRequest req = new AddToCartRequest();
        Cart cart = new Cart();
        when(cartService.addItem(eq(req))).thenReturn(cart);
        when(cartMapper.entityToDTO(cart)).thenReturn(new CartDTO());

        // when
        ResponseEntity<CartDTO> response = cartController.add(req);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartService, times(1)).addItem(eq(req));
        verify(cartMapper, times(1)).entityToDTO(cart);
    }

    @Test
    void testUpdateQty() {
        // given
        Long itemId = 42L;
        UpdateCartItemRequest req = new UpdateCartItemRequest();
        req.setQuantity(7);

        Cart cart = new Cart();
        when(cartService.updateItemQuantity(eq(itemId), eq(7))).thenReturn(cart);
        when(cartMapper.entityToDTO(cart)).thenReturn(new CartDTO());

        // when
        ResponseEntity<CartDTO> response = cartController.updateQty(itemId, req);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartService, times(1)).updateItemQuantity(eq(itemId), eq(7));
        verify(cartMapper, times(1)).entityToDTO(cart);
    }

    @Test
    void testRemove() {
        // given
        Long itemId = 5L;

        // when
        ResponseEntity<Object> response = cartController.remove(itemId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartService, times(1)).removeItem(itemId);
    }

    @Test
    void testClear() {
        // when
        ResponseEntity<Object> response = cartController.clear();

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartService, times(1)).clear();
    }
}