package com.paklog.productcatalog.application.command;

import com.paklog.productcatalog.domain.model.Attributes;
import com.paklog.productcatalog.domain.model.Dimensions;
import com.paklog.productcatalog.domain.model.SKU;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductCommand(
    @NotNull @Valid SKU sku,
    @NotBlank String title,
    @Valid Dimensions dimensions,
    @Valid Attributes attributes
) {
    
    public static CreateProductCommand of(SKU sku, String title, Dimensions dimensions, Attributes attributes) {
        return new CreateProductCommand(sku, title, dimensions, attributes);
    }
    
    public static CreateProductCommand of(SKU sku, String title) {
        return new CreateProductCommand(sku, title, null, null);
    }
}