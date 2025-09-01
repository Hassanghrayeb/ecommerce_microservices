package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.mapper.ProductMapper;
import com.hamster.ecommerce.model.dto.ProductDTO;
import com.hamster.ecommerce.model.entity.Product;
import com.hamster.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product Controller", description = "Operations pertaining to products")
@RestController
@RequestMapping("/product")
public class ProductController
{
    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper)
    {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @Operation(summary = "Get all products, in paged format")
    @GetMapping("")
    ResponseEntity<Page<ProductDTO>> findAllPaged(@PageableDefault(page = 0, size = 10) Pageable pageable)
    {
        Page<Product> productPage = productService.findAllByEnabled(pageable);
        return new ResponseEntity<>(productPage.map(productMapper::entityToDTO), HttpStatus.OK);
    }
}
