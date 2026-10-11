package com.freshsupply.fms.controller;

import com.freshsupply.fms.dto.OrderRequest;
import com.freshsupply.fms.dto.OrderResponse;
import com.freshsupply.fms.dto.StatusUpdateRequest;
import com.freshsupply.fms.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service; // depends on the interface

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrderResponse> list() { return service.findAll(); }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(@Valid @RequestBody OrderRequest request) { return service.placeOrder(request); }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return service.updateStatus(id, request.status());
    }
}
