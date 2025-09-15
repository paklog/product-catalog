package com.paklog.productcatalog.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DimensionsTest {

    @Test
    void shouldCreateValidDimensions() {
        // Given valid dimensions where item fits in package
        var itemDimensions = createDimensionSet(
            BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0), BigDecimal.valueOf(2.0), BigDecimal.valueOf(1.0)
        );
        var packageDimensions = createDimensionSet(
            BigDecimal.valueOf(6.0), BigDecimal.valueOf(4.0), BigDecimal.valueOf(3.0), BigDecimal.valueOf(1.5)
        );

        // When creating dimensions
        assertDoesNotThrow(() -> new Dimensions(itemDimensions, packageDimensions));
    }

    @Test
    void shouldRejectWhenItemLengthExceedsPackageLength() {
        // Given item length > package length
        var itemDimensions = createDimensionSet(
            BigDecimal.valueOf(10.0), BigDecimal.valueOf(3.0), BigDecimal.valueOf(2.0), BigDecimal.valueOf(1.0)
        );
        var packageDimensions = createDimensionSet(
            BigDecimal.valueOf(6.0), BigDecimal.valueOf(4.0), BigDecimal.valueOf(3.0), BigDecimal.valueOf(1.5)
        );

        // When creating dimensions
        var exception = assertThrows(IllegalArgumentException.class, 
            () -> new Dimensions(itemDimensions, packageDimensions));

        // Then
        assertEquals("Item dimensions cannot be larger than package dimensions", exception.getMessage());
    }

    @Test
    void shouldRejectWhenItemWidthExceedsPackageWidth() {
        // Given item width > package width
        var itemDimensions = createDimensionSet(
            BigDecimal.valueOf(5.0), BigDecimal.valueOf(8.0), BigDecimal.valueOf(2.0), BigDecimal.valueOf(1.0)
        );
        var packageDimensions = createDimensionSet(
            BigDecimal.valueOf(6.0), BigDecimal.valueOf(4.0), BigDecimal.valueOf(3.0), BigDecimal.valueOf(1.5)
        );

        // When creating dimensions
        var exception = assertThrows(IllegalArgumentException.class, 
            () -> new Dimensions(itemDimensions, packageDimensions));

        // Then
        assertEquals("Item dimensions cannot be larger than package dimensions", exception.getMessage());
    }

    @Test
    void shouldRejectWhenItemHeightExceedsPackageHeight() {
        // Given item height > package height
        var itemDimensions = createDimensionSet(
            BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0), BigDecimal.valueOf(6.0), BigDecimal.valueOf(1.0)
        );
        var packageDimensions = createDimensionSet(
            BigDecimal.valueOf(6.0), BigDecimal.valueOf(4.0), BigDecimal.valueOf(3.0), BigDecimal.valueOf(1.5)
        );

        // When creating dimensions
        var exception = assertThrows(IllegalArgumentException.class, 
            () -> new Dimensions(itemDimensions, packageDimensions));

        // Then
        assertEquals("Item dimensions cannot be larger than package dimensions", exception.getMessage());
    }

    @Test
    void shouldAllowEqualDimensions() {
        // Given equal item and package dimensions
        var itemDimensions = createDimensionSet(
            BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0), BigDecimal.valueOf(2.0), BigDecimal.valueOf(1.0)
        );
        var packageDimensions = createDimensionSet(
            BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0), BigDecimal.valueOf(2.0), BigDecimal.valueOf(1.0)
        );

        // When creating dimensions
        assertDoesNotThrow(() -> new Dimensions(itemDimensions, packageDimensions));
    }

    @Test
    void shouldRejectNullItemDimensions() {
        var packageDimensions = createDimensionSet(
            BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0), BigDecimal.valueOf(2.0), BigDecimal.valueOf(1.0)
        );

        var exception = assertThrows(NullPointerException.class, 
            () -> new Dimensions(null, packageDimensions));

        assertEquals("Item dimensions cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullPackageDimensions() {
        var itemDimensions = createDimensionSet(
            BigDecimal.valueOf(5.0), BigDecimal.valueOf(3.0), BigDecimal.valueOf(2.0), BigDecimal.valueOf(1.0)
        );

        var exception = assertThrows(NullPointerException.class, 
            () -> new Dimensions(itemDimensions, null));

        assertEquals("Package dimensions cannot be null", exception.getMessage());
    }

    private DimensionSet createDimensionSet(BigDecimal length, BigDecimal width, BigDecimal height, BigDecimal weight) {
        return new DimensionSet(
            new DimensionMeasurement(length, DimensionMeasurement.DimensionUnit.INCHES),
            new DimensionMeasurement(width, DimensionMeasurement.DimensionUnit.INCHES),
            new DimensionMeasurement(height, DimensionMeasurement.DimensionUnit.INCHES),
            new WeightMeasurement(weight, WeightMeasurement.WeightUnit.POUNDS)
        );
    }
}