package com.paklog.productcatalog.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response payload")
public record ErrorDto(
    @Schema(description = "Numeric error code aligned with HTTP status", example = "400")
    int code,

    @Schema(description = "Human-readable explanation of the error", example = "Validation failed: sku must not be blank")
    String message
) {
    
    public static ErrorDto of(int code, String message) {
        return new ErrorDto(code, message);
    }
}