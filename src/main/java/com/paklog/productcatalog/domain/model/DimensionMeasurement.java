package com.paklog.productcatalog.domain.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

public record DimensionMeasurement(
    @Positive @NotNull BigDecimal value,
    @NotNull DimensionUnit unit
) {
    
    public DimensionMeasurement {
        Objects.requireNonNull(value, "Dimension value cannot be null");
        Objects.requireNonNull(unit, "Dimension unit cannot be null");
        
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Dimension value must be positive");
        }
    }
    
    public static DimensionMeasurement of(BigDecimal value, DimensionUnit unit) {
        return new DimensionMeasurement(value, unit);
    }
    
    public static DimensionMeasurement of(double value, DimensionUnit unit) {
        return new DimensionMeasurement(BigDecimal.valueOf(value), unit);
    }
    
    public enum DimensionUnit {
        INCHES, CENTIMETERS, MILLIMETERS, FEET, METERS
    }
}