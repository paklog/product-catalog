package com.paklog.productcatalog.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "A paged response containing products")
public record ProductPageDto(
    @Schema(description = "The list of products for this page")
    List<ProductDto> content,
    
    @Schema(description = "Total number of pages available")
    int totalPages,
    
    @Schema(description = "Total number of products across all pages")
    long totalElements,
    
    @Schema(description = "Current page number (0-based)")
    int currentPage,
    
    @Schema(description = "Number of items per page")
    int pageSize,
    
    @Schema(description = "Whether this is the first page")
    boolean isFirst,
    
    @Schema(description = "Whether this is the last page")
    boolean isLast
) {}