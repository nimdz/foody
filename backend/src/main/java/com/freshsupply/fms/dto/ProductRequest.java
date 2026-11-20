package com.freshsupply.fms.dto;

import com.freshsupply.fms.domain.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String sku,
        @NotBlank String name,
        @NotNull Category category,
        @NotBlank String unit,
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice,
        @Min(0) int stockQuantity,
        @Min(0) int reorderLevel,
        Long supplierId) { }
