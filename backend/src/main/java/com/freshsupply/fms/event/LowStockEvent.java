package com.freshsupply.fms.event;

public record LowStockEvent(Long productId, String productName, int stockQuantity, int reorderLevel) { }
