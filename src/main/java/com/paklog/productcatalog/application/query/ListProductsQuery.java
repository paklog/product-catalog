package com.paklog.productcatalog.application.query;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record ListProductsQuery(
    @Min(0) int offset,
    @Min(1) int limit
) {
    
    public ListProductsQuery {
        if (limit > 100) {
            throw new IllegalArgumentException("Limit cannot exceed 100");
        }
    }
    
    public static ListProductsQuery of(int offset, int limit) {
        return new ListProductsQuery(offset, limit);
    }
    
    public Pageable toPageable() {
        return PageRequest.of(offset / limit, limit);
    }
}