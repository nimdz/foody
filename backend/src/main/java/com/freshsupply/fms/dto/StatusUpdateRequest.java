package com.freshsupply.fms.dto;

import com.freshsupply.fms.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull OrderStatus status) { }
