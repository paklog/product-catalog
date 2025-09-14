package com.paklog.productcatalog.domain.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

public record WeightMeasurement(
    @Positive @NotNull BigDecimal value,
    @NotNull WeightUnit unit
) {
    
    public WeightMeasurement {
        Objects.requireNonNull(value, "Weight value cannot be null");
        Objects.requireNonNull(unit, "Weight unit cannot be null");
        
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Weight value must be positive");
        }
    }
    
    public static WeightMeasurement of(BigDecimal value, WeightUnit unit) {
        return new WeightMeasurement(value, unit);
    }
    
    public static WeightMeasurement of(double value, WeightUnit unit) {
        return new WeightMeasurement(BigDecimal.valueOf(value), unit);
    }
    
    public enum WeightUnit {
        POUNDS, KILOGRAMS, GRAMS, OUNCES
    }
}