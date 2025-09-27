package com.paklog.productcatalog.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "The core Product aggregate root, representing a unique item in the catalog")
public record ProductDto(
    @Schema(description = "The unique, seller-defined Stock Keeping Unit", example = "EXAMPLE-SKU-123")
    @NotBlank String sku,
    
    @Schema(description = "The display name of the product", example = "Industrial Grade Widget")
    @NotBlank String title,
    
    @Valid @NotNull DimensionsDto dimensions,
    @Valid @NotNull AttributesDto attributes
) {
    
    @Schema(description = "A Value Object containing the physical dimensions of the item and its packaging")
    public record DimensionsDto(
        @Schema(description = "The dimensions of the product itself, without packaging")
        @Valid @NotNull DimensionSetDto item,

        @Schema(description = "The dimensions of the product in its shippable packaging")
        @JsonProperty("package") @Valid @NotNull DimensionSetDto packageDimensions
    ) {}
    
    @Schema(description = "A complete set of measurements for an object")
    public record DimensionSetDto(
        @Valid @NotNull DimensionMeasurementDto length,
        @Valid @NotNull DimensionMeasurementDto width,
        @Valid @NotNull DimensionMeasurementDto height,
        @Valid @NotNull WeightMeasurementDto weight
    ) {}
    
    @Schema(description = "A measurement of length, width, or height")
    public record DimensionMeasurementDto(
        @Schema(example = "10.5")
        @Positive @NotNull BigDecimal value,
        
        @Schema(example = "INCHES")
        @NotNull DimensionUnitDto unit
    ) {}
    
    @Schema(description = "A measurement of weight")
    public record WeightMeasurementDto(
        @Schema(example = "5.8")
        @Positive @NotNull BigDecimal value,
        
        @Schema(example = "POUNDS")
        @NotNull WeightUnitDto unit
    ) {}
    
    @Schema(description = "A Value Object for storing additional product characteristics and compliance data")
    public record AttributesDto(
        @JsonProperty("hazmat_info") @Valid @NotNull HazmatInfoDto hazmatInfo
    ) {}
    
    @Schema(description = "Information related to hazardous material classification")
    public record HazmatInfoDto(
        @JsonProperty("is_hazmat") @Schema(example = "true")
        boolean isHazmat,
        
        @JsonProperty("un_number") @Schema(description = "The UN number for the hazardous material, if applicable", example = "UN1950")
        String unNumber
    ) {}
    
    public enum DimensionUnitDto {
        INCHES, CENTIMETERS, MILLIMETERS, FEET, METERS
    }
    
    public enum WeightUnitDto {
        POUNDS, KILOGRAMS, GRAMS, OUNCES
    }
}