package com.freshsupply.fms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SupplierRequest(
        @NotBlank String name,
        @Email String contactEmail,
        String phone,
        String address) { }
