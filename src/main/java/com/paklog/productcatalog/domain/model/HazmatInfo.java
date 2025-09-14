package com.paklog.productcatalog.domain.model;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public record HazmatInfo(
    boolean isHazmat,
    String unNumber
) {
    
    public HazmatInfo {
        if (isHazmat && (unNumber == null || unNumber.trim().isEmpty())) {
            throw new IllegalArgumentException(
                "UN number is required when item is classified as hazardous material"
            );
        }
        
        if (!isHazmat && unNumber != null) {
            throw new IllegalArgumentException(
                "UN number should not be provided when item is not hazardous material"
            );
        }
    }
    
    public static HazmatInfo nonHazmat() {
        return new HazmatInfo(false, null);
    }
    
    public static HazmatInfo hazmat(@NotBlank String unNumber) {
        Objects.requireNonNull(unNumber, "UN number cannot be null for hazmat items");
        return new HazmatInfo(true, unNumber);
    }
}