package com.paklog.productcatalog.domain.model;

import jakarta.validation.Valid;
import java.util.Objects;

public record Dimensions(
    @Valid DimensionSet item,
    @Valid DimensionSet packageDimensions
) {
    
    public Dimensions {
        Objects.requireNonNull(item, "Item dimensions cannot be null");
        Objects.requireNonNull(packageDimensions, "Package dimensions cannot be null");
        
        validateItemFitsInPackage(item, packageDimensions);
    }
    
    private void validateItemFitsInPackage(DimensionSet item, DimensionSet packageDimensions) {
        if (item.length().value().compareTo(packageDimensions.length().value()) > 0 ||
            item.width().value().compareTo(packageDimensions.width().value()) > 0 ||
            item.height().value().compareTo(packageDimensions.height().value()) > 0) {
            
            throw new IllegalArgumentException(
                "Item dimensions cannot be larger than package dimensions"
            );
        }
    }
    
    public static Dimensions of(DimensionSet item, DimensionSet packageDimensions) {
        return new Dimensions(item, packageDimensions);
    }
}