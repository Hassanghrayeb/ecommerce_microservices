package com.hamster.ecommerce.repository;

import com.hamster.ecommerce.model.entity.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends PagingAndSortingRepository<Cart, Long>, CrudRepository<Cart, Long>
{
    Optional<Cart> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
