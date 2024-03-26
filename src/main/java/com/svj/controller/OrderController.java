package com.svj.controller;

import com.svj.dto.OrderRequestDTO;
import com.svj.dto.OrderResponseDTO;
import com.svj.entity.Order;
import com.svj.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderService service;

    public OrderController(OrderService service){
        this.service= service;
    }

    // validation
    // logging
    // Exception handling
    @PostMapping
    public String placeNewOrder(@RequestBody @Valid OrderRequestDTO order){
        return service.placeOrder(order);
    }

    @GetMapping("/{orderId}")
    public OrderResponseDTO getOrder(@PathVariable String orderId){
        return service.getOrder(orderId);
    }
}
