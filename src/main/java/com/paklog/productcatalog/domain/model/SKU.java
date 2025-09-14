package com.paklog.productcatalog.domain.model;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record SKU(@NotBlank String value) {
    
    public SKU {
        Objects.requireNonNull(value, "SKU cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be empty");
        }
    }
    
    public static SKU of(String value) {
        return new SKU(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}