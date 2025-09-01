package com.hamster.ecommerce.service;

import com.hamster.ecommerce.model.dto.SimpleIdStatusDTO;
import com.hamster.ecommerce.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService
{
    Page<Product> find(Pageable pageable);
    Optional<Product> findById(Long id);
    Optional<Product> findByProductName(String productName);
    Product saveProduct(Product product);
    void delete(Long id);
    void updateStatus(SimpleIdStatusDTO dto);
    Page<Product> findAllByEnabled(Pageable pageable);
    Page<Product> findLowStock(Pageable pageable);

}
