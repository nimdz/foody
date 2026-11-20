package com.freshsupply.fms.dto;

import com.freshsupply.fms.domain.CustomerTier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        String phone,
        String address,
        @NotNull CustomerTier tier) { }
