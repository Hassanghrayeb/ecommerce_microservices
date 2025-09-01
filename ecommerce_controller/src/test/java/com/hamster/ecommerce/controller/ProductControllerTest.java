package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.mapper.ProductMapper;
import com.hamster.ecommerce.model.dto.ProductDTO;
import com.hamster.ecommerce.model.entity.Product;
import com.hamster.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    @Mock
    private ProductMapper productMapper;
    @Mock private ProductService productService;

    @InjectMocks
    private AdminProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllPaged() {
        Page<Product> page = new PageImpl<>(List.of(new Product()));
        when(productService.find(any())).thenReturn(page);
        when(productMapper.entityToDTO(any(Product.class))).thenReturn(new ProductDTO());

        ResponseEntity<Page<ProductDTO>> response =
                productController.findAllPaged(PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getTotalElements());
        verify(productService, times(1)).find(any());
    }

    @Test
    void testFindOne() {
        Product p = new Product();
        when(productService.findById(1L)).thenReturn(Optional.of(p));
        when(productMapper.entityToDTO(p)).thenReturn(new ProductDTO());

        ResponseEntity<ProductDTO> response = productController.findOne(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService, times(1)).findById(1L);
    }

    @Test
    void testFindOne_NotFound() {
        when(productService.findById(999L)).thenReturn(Optional.empty());

        try {
            productController.findOne(999L);
        } catch (NotFoundException e) {
            assertEquals("Item not found : 999", e.getMessage());
        }

        verify(productService, times(1)).findById(999L);
    }

    @Test
    void testCreate() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Mouse");
        dto.setPrice(BigDecimal.valueOf(19.99));

        Product entity = new Product();
        when(productMapper.dtoToEntity(dto)).thenReturn(entity);
        when(productService.saveProduct(entity)).thenReturn(entity);
        when(productMapper.entityToDTO(entity)).thenReturn(dto);

        ResponseEntity<ProductDTO> response = productController.saveProduct(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(productService, times(1)).saveProduct(entity);
    }

    @Test
    void testUpdate() {
        ProductDTO dto = new ProductDTO();
        Product entity = new Product();
        when(productService.findById(1L)).thenReturn(Optional.of(entity));
        when(productService.saveProduct(entity)).thenReturn(entity);
        when(productMapper.entityToDTO(entity)).thenReturn(dto);

        ResponseEntity<Object> response = productController.updateProduct(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService, times(1)).saveProduct(entity);
    }

    @Test
    void testUpdate_NotFound() {
        when(productService.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = productController.updateProduct(1L, new ProductDTO());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService, never()).saveProduct(any());
    }
}
