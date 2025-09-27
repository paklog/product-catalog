package com.paklog.productcatalog.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paged response metadata and product entries")
public record ProductPageDto(
    @Schema(description = "Products for the current page")
    List<ProductDto> content,

    @Schema(description = "Total number of pages available")
    @JsonProperty("total_pages") int totalPages,

    @Schema(description = "Total number of products across all pages")
    @JsonProperty("total_elements") long totalElements,

    @Schema(description = "Current page number (0-based)")
    @JsonProperty("current_page") int currentPage,

    @Schema(description = "Number of items per page")
    @JsonProperty("page_size") int pageSize,

    @Schema(description = "Indicates whether this is the first page")
    @JsonProperty("is_first") boolean isFirst,

    @Schema(description = "Indicates whether this is the last page")
    @JsonProperty("is_last") boolean isLast
) {}