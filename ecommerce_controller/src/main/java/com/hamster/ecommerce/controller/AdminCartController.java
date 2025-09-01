package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.mapper.CartMapper;
import com.hamster.ecommerce.model.dto.CartDTO;
import com.hamster.ecommerce.model.entity.Cart;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Cart Controller", description = "Operations pertaining to carts")
@RestController
@RequestMapping("admin/cart")
public class AdminCartController
{
    private final CartService cartService;
    private final CartMapper cartMapper;

    public AdminCartController(CartService cartService, CartMapper cartMapper)
    {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }

    @Operation(summary = "Get all carts, in paged format")
    @GetMapping("")
    ResponseEntity<Page<CartDTO>> findAllPaged(@PageableDefault(page = 0, size = 10) Pageable pageable)
    {
        Page<Cart> cartPage = cartService.find(pageable);
        return new ResponseEntity<>(cartPage.map(cartMapper::entityToDTO), HttpStatus.OK);
    }

    @Operation(summary = "clear User Cart")
    @DeleteMapping("/clear")
    public ResponseEntity<Object> clear() {
        cartService.clear();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "clear User Cart")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        cartService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
