package com.freshsupply.fms.dto;

import com.freshsupply.fms.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id, Long customerId, String customerName, OrderStatus status,
        List<OrderItemResponse> items, BigDecimal subtotal, BigDecimal discountAmount, BigDecimal total,
        LocalDateTime createdAt) { }
