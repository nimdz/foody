package com.freshsupply.fms.dto;

import com.freshsupply.fms.domain.Category;

import java.math.BigDecimal;

public record ProductResponse(
        Long id, String sku, String name, Category category, String unit, BigDecimal unitPrice,
        int stockQuantity, int reorderLevel, boolean lowStock, Long supplierId, String supplierName) { }
