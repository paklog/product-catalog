package com.paklog.productcatalog.application.command;

import com.paklog.productcatalog.domain.model.Attributes;
import com.paklog.productcatalog.domain.model.Dimensions;
import com.paklog.productcatalog.domain.model.SKU;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record PatchProductCommand(
    @NotNull @Valid SKU sku,
    Optional<String> title,
    Optional<@Valid Dimensions> dimensions,
    Optional<@Valid Attributes> attributes
) {
    
    public static PatchProductCommand of(SKU sku, Optional<String> title, 
                                       Optional<Dimensions> dimensions, 
                                       Optional<Attributes> attributes) {
        return new PatchProductCommand(sku, title, dimensions, attributes);
    }
}