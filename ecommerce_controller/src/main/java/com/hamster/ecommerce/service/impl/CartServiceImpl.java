package com.hamster.ecommerce.service.impl;

import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.model.entity.Cart;
import com.hamster.ecommerce.model.entity.CartItem;
import com.hamster.ecommerce.model.entity.Product;
import com.hamster.ecommerce.model.simple.AddToCartRequest;
import com.hamster.ecommerce.repository.CartItemRepository;
import com.hamster.ecommerce.repository.CartRepository;
import com.hamster.ecommerce.repository.ProductRepository;
import com.hamster.ecommerce.service.CartService;
import com.hamster.ecommerce.util.AuditUtil;
import com.hamster.ecommerce.util.ContextUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService
{
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ContextUtil contextUtil;
    private final AuditUtil auditUtil;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, CartItemRepository cartItemRepository, ContextUtil contextUtil, AuditUtil auditUtil)
    {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.contextUtil = contextUtil;
        this.auditUtil = auditUtil;
    }

    @Override
    public Page<Cart> find(Pageable pageable)
    {
        Page<Cart> cartPage =  cartRepository.findAll(pageable);
        return cartPage.map(this::populateCart);
    }

    private Cart populateCart(Cart cart)
    {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        cart.setItems(items);
        return cart;
    }

    @Override
    public Cart findMyCart()
    {
        Long userId = contextUtil.getCurrentUserId();
        return getOrCreate(userId);
    }

    @Override
    public Optional<Cart> findByUserId(Long userId)
    {
        return cartRepository.findByUserId(userId);
    }

    @Override
    public Cart saveCart(Cart cart)
    {
        auditUtil.setAuditColumns(cart);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional("postgresTransactionManager")
    public void clear() {
        Long userId = contextUtil.getCurrentUserId();

        Long cartId = cartRepository.findByUserId(userId)
                .map(Cart::getId)
                .orElseThrow(() -> new NotFoundException("Cart not found for userId " + userId));

        cartItemRepository.deleteAllForCartId(cartId);
    }

    @Override
    @Transactional("postgresTransactionManager")
    public Cart removeItem(Long cartItemId)
    {
        Long userId = contextUtil.getCurrentUserId();
        Cart cart = getOrCreate(userId);
        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow(() -> new NotFoundException("Item not found with Id" + cartItemId));

        if (!cart.getId().equals(item.getCartId())) throw new IllegalArgumentException("Item not in your cart");
        cartItemRepository.deleteById(cartItemId);
        cart.setItems(cartItemRepository.findByCartId(cart.getId()));

        return cart;
    }

    @Override
    public Cart getOrCreate(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            auditUtil.setAuditColumns(newCart);
            return cartRepository.save(newCart);
        });
        cart.setItems(cartItemRepository.findByCartId(cart.getId()));
        return cart;
    }

    @Override
    @Transactional("postgresTransactionManager")
    public void delete(Long id)
    {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with Id" + id));
        cartItemRepository.deleteAllForCartId(id);
        cartRepository.deleteById(id);
    }

    @Override
    @Transactional("postgresTransactionManager")
    public Cart updateItemQuantity(Long cartItemId, int quantity)
    {
        Long userId = contextUtil.getCurrentUserId();

        Cart cart = getOrCreate(userId);
        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow(() -> new IllegalArgumentException("Item not found"));
        if (!cart.getId().equals(item.getCartId())) throw new IllegalArgumentException("Item not in your cart");

        item.setQuantity(quantity);
        auditUtil.setAuditColumns(item);
        cartItemRepository.save(item);

        cart.setItems(cartItemRepository.findByCartId(cart.getId()));
        return cart;
    }

    @Override
    @Transactional("postgresTransactionManager")
    public Cart addItem(AddToCartRequest addToCartRequest)
    {
        Long userId = contextUtil.getCurrentUserId();
        Long productId = addToCartRequest.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product Not Found with Id" + productId));

        Cart cart = getOrCreate(userId);

        CartItem existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId).orElse(null);
        if(Objects.nonNull(existing)) {
            existing.setQuantity(Math.addExact(addToCartRequest.getQuantity(), existing.getQuantity()));
        }
        else {
            CartItem item = new CartItem();
            item.setCartId(cart.getId());
            item.setProductId(addToCartRequest.getProductId());
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setCurrency(product.getCurrency());
            item.setQuantity(addToCartRequest.getQuantity());
            cart.getItems().add(item);
            auditUtil.setAuditColumns(item);

            cartItemRepository.save(item);
        }

        return cartRepository.save(cart);
    }
}
