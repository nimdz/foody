package com.freshsupply.fms.dto;

import java.math.BigDecimal;

public record OrderItemResponse(Long productId, String productName, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) { }
