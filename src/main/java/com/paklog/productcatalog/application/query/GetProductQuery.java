package com.paklog.productcatalog.application.query;

import com.paklog.productcatalog.domain.model.SKU;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record GetProductQuery(
    @NotNull @Valid SKU sku
) {
    
    public static GetProductQuery of(SKU sku) {
        return new GetProductQuery(sku);
    }
}