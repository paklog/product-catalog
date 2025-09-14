package com.paklog.productcatalog.application.command;

import com.paklog.productcatalog.domain.model.Attributes;
import com.paklog.productcatalog.domain.model.Dimensions;
import com.paklog.productcatalog.domain.model.SKU;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProductCommand(
    @NotNull @Valid SKU sku,
    @NotBlank String title,
    @Valid Dimensions dimensions,
    @Valid Attributes attributes
) {
    
    public static UpdateProductCommand of(SKU sku, String title, Dimensions dimensions, Attributes attributes) {
        return new UpdateProductCommand(sku, title, dimensions, attributes);
    }
}