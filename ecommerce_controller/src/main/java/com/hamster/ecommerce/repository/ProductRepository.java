package com.hamster.ecommerce.repository;

import com.hamster.ecommerce.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long>, CrudRepository<Product, Long>
{
    Page<Product> findAllByEnabledTrue(Pageable pageable);
    boolean existsBySku(String sku);
    @Query(value = "select * from products where lower(productName) = lower(:productName)")
    Optional<Product> findByProductName(String productName);
    Page<Product> findByStockLessThan(int threshold, Pageable pageable);
}
