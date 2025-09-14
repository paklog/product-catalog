package com.paklog.productcatalog.domain.model;

import jakarta.validation.Valid;
import java.util.Objects;

public record DimensionSet(
    @Valid DimensionMeasurement length,
    @Valid DimensionMeasurement width,
    @Valid DimensionMeasurement height,
    @Valid WeightMeasurement weight
) {
    
    public DimensionSet {
        Objects.requireNonNull(length, "Length cannot be null");
        Objects.requireNonNull(width, "Width cannot be null");
        Objects.requireNonNull(height, "Height cannot be null");
        Objects.requireNonNull(weight, "Weight cannot be null");
    }
    
    public static DimensionSet of(
        DimensionMeasurement length,
        DimensionMeasurement width,
        DimensionMeasurement height,
        WeightMeasurement weight
    ) {
        return new DimensionSet(length, width, height, weight);
    }
}