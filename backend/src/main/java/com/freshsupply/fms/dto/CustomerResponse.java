package com.freshsupply.fms.dto;

import com.freshsupply.fms.domain.CustomerTier;

public record CustomerResponse(Long id, String name, String email, String phone, String address, CustomerTier tier) { }
