package com.paklog.productcatalog.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response")
public record ErrorDto(
    @Schema(description = "Error code")
    int code,
    
    @Schema(description = "Error message")
    String message
) {
    
    public static ErrorDto of(int code, String message) {
        return new ErrorDto(code, message);
    }
}