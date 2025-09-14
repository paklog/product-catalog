package com.paklog.productcatalog.application.command;

import com.paklog.productcatalog.domain.model.SKU;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record DeleteProductCommand(
    @NotNull @Valid SKU sku
) {
    
    public static DeleteProductCommand of(SKU sku) {
        return new DeleteProductCommand(sku);
    }
}