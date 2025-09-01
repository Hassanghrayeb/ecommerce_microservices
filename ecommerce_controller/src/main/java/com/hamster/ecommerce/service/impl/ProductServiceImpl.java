package com.hamster.ecommerce.service.impl;

import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.model.dto.SimpleIdStatusDTO;
import com.hamster.ecommerce.model.entity.Product;
import com.hamster.ecommerce.repository.ProductRepository;
import com.hamster.ecommerce.service.ProductService;
import com.hamster.ecommerce.util.AuditUtil;
import com.hamster.ecommerce.util.PriceUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService
{
    private final ProductRepository productRepository;
    private final AuditUtil auditUtil;

    public ProductServiceImpl(ProductRepository productRepository, AuditUtil auditUtil)
    {
        this.productRepository = productRepository;
        this.auditUtil = auditUtil;
    }

    @Override
    public Page<Product> find(Pageable pageable)
    {
        return (Page<Product>) productRepository.findAll(pageable);
    }

    public Page<Product> findAllByEnabled(Pageable pageable)
    {
        return (Page<Product>) productRepository.findAllByEnabledTrue(pageable);
    }

    @Override
    public Page<Product> findLowStock(Pageable pageable)
    {
        int threshold = 5; //TODO
        return (Page<Product>) productRepository.findByStockLessThan(threshold, pageable);
    }

    @Override
    public Optional<Product> findById(Long id)
    {
        return productRepository.findById(id);
    }

    @Override
    public Optional<Product> findByProductName(String productName)
    {
        return productRepository.findByProductName(productName);
    }

    @Override
    public Product saveProduct(Product product)
    {
        auditUtil.setAuditColumns(product);
        product.setPrice(PriceUtil.scale(product.getPrice(), product.getCurrency()));
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id)
    {
        productRepository.deleteById(id);
    }

    @Override
    public void updateStatus(SimpleIdStatusDTO dto)
    {
        Product product = productRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(dto.getId()));

        product.setEnabled(dto.getStatus());
        productRepository.save(product);
    }
}
