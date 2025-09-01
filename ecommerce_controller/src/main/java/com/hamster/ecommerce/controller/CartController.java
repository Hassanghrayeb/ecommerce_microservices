package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.mapper.CartMapper;
import com.hamster.ecommerce.model.dto.CartDTO;
import com.hamster.ecommerce.model.entity.Cart;
import com.hamster.ecommerce.model.simple.AddToCartRequest;
import com.hamster.ecommerce.model.simple.UpdateCartItemRequest;
import com.hamster.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cart Controller", description = "Operations pertaining to carts")
@RestController
@RequestMapping("/cart")
public class CartController
{
    private final CartService cartService;
    private final CartMapper cartMapper;

    public CartController(CartService cartService, CartMapper cartMapper)
    {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }

    @Operation(summary = "Get My Cart")
    @GetMapping("")
    ResponseEntity<CartDTO> GetMyCart()
    {
        Cart cart = cartService.findMyCart();
        return new ResponseEntity<>((cartMapper.entityToDTO(cart)), HttpStatus.OK);
    }

    @Operation(summary = "Add Item To Cart")
    @PostMapping("/items")
    public ResponseEntity<CartDTO> add(@RequestBody AddToCartRequest req) {
        Cart cart = cartService.addItem(req);
        return new ResponseEntity<>(cartMapper.entityToDTO(cart), HttpStatus.OK);
    }

    @Operation(summary = "Update Cart Item Quantity")
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateQty(@PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest req) {
        Cart cart = cartService.updateItemQuantity(itemId, req.getQuantity());
        return new ResponseEntity<>(cartMapper.entityToDTO(cart), HttpStatus.OK);
    }

    @Operation(summary = "Remove Item from Cart")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Object> remove(@PathVariable Long itemId) {

        cartService.removeItem(itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "clear User Cart")
    @DeleteMapping("/clear")
    public ResponseEntity<Object> clear() {
        cartService.clear();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
