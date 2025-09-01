package com.hamster.ecommerce.controller;


import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.mapper.ProductMapper;
import com.hamster.ecommerce.model.dto.OrderDTO;
import com.hamster.ecommerce.model.dto.ProductDTO;
import com.hamster.ecommerce.model.dto.SimpleIdStatusDTO;
import com.hamster.ecommerce.model.entity.Order;
import com.hamster.ecommerce.model.entity.Product;
import com.hamster.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.util.Optional;

@Tag(name = "Admin Product Controller", description = "Operations pertaining to products")
@RestController
@RequestMapping("/admin/product")
public class AdminProductController
{
    private final ProductService productService;
    private final ProductMapper productMapper;

    public AdminProductController(ProductService productService, ProductMapper productMapper)
    {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @Operation(summary = "Get all products, in paged format")
    @GetMapping("")
    ResponseEntity<Page<ProductDTO>> findAllPaged(@PageableDefault(page = 0, size = 10) Pageable pageable)
    {
        Page<Product> productPage = productService.find(pageable);
        return new ResponseEntity<>(productPage.map(productMapper::entityToDTO), HttpStatus.OK);
    }

    @Operation(summary = "Find a single product, by id")
    @GetMapping("/{id}")
    ResponseEntity<ProductDTO> findOne(@PathVariable Long id)
    {
        Product product = productService.findById(id).orElseThrow(() -> new NotFoundException(id));
        return new ResponseEntity<>(productMapper.entityToDTO(product), HttpStatus.OK);
    }

    @Operation(summary = "Create a new product")
    @PostMapping("")
    ResponseEntity<ProductDTO> saveProduct(@Valid @RequestBody ProductDTO dto)
    {
        Product product = productService.saveProduct(productMapper.dtoToEntity(dto));
        return new ResponseEntity<>(productMapper.entityToDTO(product), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing product")
    @PutMapping("/{id}")
    ResponseEntity<Object> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO dto)
    {
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Product product = optionalProduct.get();
        dto.setId(id);
        productMapper.dtoToEntity(dto, product);
        product = productService.saveProduct(product);
        return new ResponseEntity<>(productMapper.entityToDTO(product), HttpStatus.OK);
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    ResponseEntity<Object> deleteProduct(@PathVariable Long id)
    {
        productService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Update user status")
    @PutMapping("/enabled")
    ResponseEntity<Void> updateProductStatus(@RequestBody SimpleIdStatusDTO dto)
    {
        productService.updateStatus(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get low stock Products, in paged format")
    @GetMapping("/low-stock")
    ResponseEntity<Page<ProductDTO>> getLowStock(@PageableDefault(page = 0, size = 10) Pageable pageable)
    {
        Page<Product> productPage = productService.findLowStock(pageable);
        return new ResponseEntity<>(productPage.map(productMapper::entityToDTO), HttpStatus.OK);
    }


}
