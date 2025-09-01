package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.exception.NotFoundException;
import com.hamster.ecommerce.mapper.OrderMapper;
import com.hamster.ecommerce.model.dto.OrderDTO;
import com.hamster.ecommerce.model.entity.Order;
import com.hamster.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Order Controller", description = "Operations pertaining to orders")
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController
{
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public AdminOrderController(OrderService orderService, OrderMapper orderMapper)
    {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @Operation(summary = "Get all orders, in paged format")
    @GetMapping("")
    ResponseEntity<Page<OrderDTO>> findAllPaged(@PageableDefault(page = 0, size = 10) Pageable pageable)
    {
        Page<Order> orderPage = orderService.find(pageable);
        return new ResponseEntity<>(orderPage.map(orderMapper::entityToDTO), HttpStatus.OK);
    }

    @Operation(summary = "Find a single order, by id")
    @GetMapping("/{id}")
    ResponseEntity<OrderDTO> findOne(@PathVariable Long id)
    {
        Order order = orderService.findById(id).orElseThrow(() -> new NotFoundException(id));
        return new ResponseEntity<>(orderMapper.entityToDTO(order), HttpStatus.OK);
    }

}
