package com.paklog.productcatalog.domain.model;

import jakarta.validation.Valid;
import java.util.Objects;

public record Attributes(
    @Valid HazmatInfo hazmatInfo
) {
    
    public Attributes {
        Objects.requireNonNull(hazmatInfo, "Hazmat info cannot be null");
    }
    
    public static Attributes of(HazmatInfo hazmatInfo) {
        return new Attributes(hazmatInfo);
    }
    
    public static Attributes withoutHazmat() {
        return new Attributes(HazmatInfo.nonHazmat());
    }
}