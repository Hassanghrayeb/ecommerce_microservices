package com.hamster.ecommerce.repository;

import com.hamster.ecommerce.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order, Long>, CrudRepository<Order, Long>
{
    Page<Order> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
