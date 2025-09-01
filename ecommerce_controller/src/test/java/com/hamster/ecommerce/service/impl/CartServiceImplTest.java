package com.hamster.ecommerce.service.impl;

import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.model.entity.Cart;
import com.hamster.ecommerce.model.entity.CartItem;
import com.hamster.ecommerce.model.entity.Product;
import com.hamster.ecommerce.model.simple.AddToCartRequest;
import com.hamster.ecommerce.repository.CartItemRepository;
import com.hamster.ecommerce.repository.CartRepository;
import com.hamster.ecommerce.repository.ProductRepository;
import com.hamster.ecommerce.util.AuditUtil;
import com.hamster.ecommerce.util.ContextUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    @Mock private CartRepository cartRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ContextUtil contextUtil;
    @Mock private AuditUtil auditUtil;

    @InjectMocks
    private CartServiceImpl cartService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFind_populatesItems() {
        Cart c = new Cart();
        c.setId(1L);

        Page<Cart> page = new PageImpl<>(List.of(c));
        when(cartRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(cartItemRepository.findByCartId(1L)).thenReturn(List.of(new CartItem()));

        Page<Cart> result = cartService.find(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertNotNull(result.getContent().get(0).getItems());
        assertEquals(1, result.getContent().get(0).getItems().size());
        verify(cartRepository, times(1)).findAll(any(Pageable.class));
        verify(cartItemRepository, times(1)).findByCartId(1L);
    }

    @Test
    void testFindByUserId_delegatesToRepo() {
        when(cartRepository.findByUserId(5L)).thenReturn(Optional.of(new Cart()));
        Optional<Cart> res = cartService.findByUserId(5L);
        assertTrue(res.isPresent());
        verify(cartRepository).findByUserId(5L);
    }

    @Test
    void testSaveCart_setsAuditAndSaves() {
        Cart cart = new Cart();
        doNothing().when(auditUtil).setAuditColumns(cart);
        when(cartRepository.save(cart)).thenReturn(cart);

        Cart saved = cartService.saveCart(cart);

        assertSame(cart, saved);
        verify(auditUtil).setAuditColumns(cart);
        verify(cartRepository).save(cart);
    }

    @Test
    void testClear_deletesItemsForCurrentUsersCart() {
        Long userId = 10L;
        Cart cart = new Cart();
        cart.setId(99L);

        when(contextUtil.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        cartService.clear();

        verify(cartItemRepository, times(1)).deleteAllForCartId(99L);
    }

    @Test
    void testClear_cartMissing_throwsNotFound() {
        when(contextUtil.getCurrentUserId()).thenReturn(77L);
        when(cartRepository.findByUserId(77L)).thenReturn(Optional.empty());

        NotFoundException ex =
                assertThrows(NotFoundException.class, () -> cartService.clear());
        assertTrue(ex.getMessage().contains("77"));

        verify(cartItemRepository, never()).deleteAllForCartId(anyLong());
    }

    @Test
    void testGetOrCreate_existingCart_setsItemsAndReturns() {
        Long userId = 3L;
        Cart existing = new Cart();
        existing.setId(123L);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(cartItemRepository.findByCartId(123L)).thenReturn(List.of(new CartItem()));

        Cart res = cartService.getOrCreate(userId);

        assertEquals(123L, res.getId());
        assertEquals(1, res.getItems().size());
        verify(auditUtil, never()).setAuditColumns(any(Cart.class)); // no new cart created
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void testGetOrCreate_createsNew_whenMissing() {
        Long userId = 4L;

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        // Save returns the new cart with id
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> {
            Cart toSave = inv.getArgument(0);
            toSave.setId(111L);
            return toSave;
        });
        when(cartItemRepository.findByCartId(111L)).thenReturn(Collections.emptyList());

        Cart res = cartService.getOrCreate(userId);

        assertEquals(111L, res.getId());
        assertEquals(userId, res.getUserId());
        verify(auditUtil, times(1)).setAuditColumns(any(Cart.class));
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(cartItemRepository, times(1)).findByCartId(111L);
    }

    @Test
    void testRemoveItem_itemNotInUsersCart_throws() {
        Long userId = 9L;
        Long userCartId = 300L;
        Long itemId = 55L;

        Cart cart = new Cart();
        cart.setId(userCartId);
        cart.setUserId(userId);

        when(contextUtil.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(userCartId)).thenReturn(new ArrayList<>());

        CartItem foreignItem = new CartItem();
        foreignItem.setId(itemId);
        foreignItem.setCartId(999L); // belongs to someone else
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(foreignItem));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> cartService.removeItem(itemId));
        assertTrue(ex.getMessage().contains("Item not in your cart"));

        verify(cartItemRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateItemQuantity_updatesWhenOwnedByUser() {
        Long userId = 6L;
        Long cartId = 222L;
        Long itemId = 12L;

        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setUserId(userId);

        when(contextUtil.getCurrentUserId()).thenReturn(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(cartId)).thenReturn(new ArrayList<>());

        CartItem item = new CartItem();
        item.setId(itemId);
        item.setCartId(cartId);
        item.setQuantity(1);
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(cartItemRepository.findByCartId(cartId)).thenReturn(List.of(item)); // after save reload

        doNothing().when(auditUtil).setAuditColumns(item);
        when(cartItemRepository.save(item)).thenReturn(item);

        Cart res = cartService.updateItemQuantity(itemId, 7);

        assertEquals(7, res.getItems().get(0).getQuantity());
        verify(auditUtil).setAuditColumns(item);
        verify(cartItemRepository).save(item);
    }


    @Test
    void testAddItem_existing_addsQuantity_andSavesCartOnly() {
        Long userId = 1L;
        Long cartId = 10L;
        Long productId = 100L;

        AddToCartRequest req = new AddToCartRequest();
        req.setProductId(productId);
        req.setQuantity(3);

        Product product = new Product();
        product.setId(productId);
        product.setName("Mouse");
        product.setPrice(new BigDecimal("10.00"));
        product.setCurrency("USD");

        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());

        CartItem existing = new CartItem();
        existing.setId(50L);
        existing.setCartId(cartId);
        existing.setProductId(productId);
        existing.setQuantity(2);
        cart.getItems().add(existing);

        when(contextUtil.getCurrentUserId()).thenReturn(userId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(cartId)).thenReturn(cart.getItems());
        when(cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.of(existing));

        when(cartRepository.save(cart)).thenReturn(cart);

        Cart res = cartService.addItem(req);

        assertEquals(5, existing.getQuantity()); // 2 + 3
        assertSame(cart, res);
        verify(cartItemRepository, never()).save(argThat(i -> !Objects.equals(i.getId(), existing.getId())));
        verify(auditUtil, never()).setAuditColumns(argThat(o -> o instanceof CartItem && ((CartItem) o).getId() == null));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testAddItem_newItem_createsAndSavesItemAndCart() {
        Long userId = 2L;
        Long cartId = 20L;
        Long productId = 200L;

        AddToCartRequest req = new AddToCartRequest();
        req.setProductId(productId);
        req.setQuantity(4);

        Product product = new Product();
        product.setId(productId);
        product.setName("Keyboard");
        product.setPrice(new BigDecimal("25.00"));
        product.setCurrency("USD");

        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());

        when(contextUtil.getCurrentUserId()).thenReturn(userId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(cartId)).thenReturn(cart.getItems());
        when(cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.empty());

        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cartRepository.save(cart)).thenReturn(cart);

        Cart res = cartService.addItem(req);

        assertSame(cart, res);
        assertEquals(1, cart.getItems().size());
        CartItem item = cart.getItems().get(0);
        assertEquals(productId, item.getProductId());
        assertEquals("Keyboard", item.getProductName());
        assertEquals(new BigDecimal("25.00"), item.getUnitPrice());
        assertEquals("USD", item.getCurrency());
        assertEquals(4, item.getQuantity());

        verify(auditUtil, times(1)).setAuditColumns(item);
        verify(cartItemRepository, times(1)).save(item);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testAddItem_productMissing_throwsNotFound() {
        Long userId = 2L;
        when(contextUtil.getCurrentUserId()).thenReturn(userId);

        AddToCartRequest req = new AddToCartRequest();
        req.setProductId(999L);
        req.setQuantity(1);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException ex =
                assertThrows(NotFoundException.class, () -> cartService.addItem(req));
        assertTrue(ex.getMessage().contains("999"));

        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }
}