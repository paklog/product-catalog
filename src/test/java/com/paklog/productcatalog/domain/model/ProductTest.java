package com.paklog.productcatalog.domain.model;

import com.paklog.productcatalog.domain.event.ProductCreatedEvent;
import com.paklog.productcatalog.domain.event.ProductUpdatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Product Domain Model Tests")
class ProductTest {
    
    private final SKU validSku = SKU.of("TEST-SKU-123");
    private final String validTitle = "Test Product";
    
    @Nested
    @DisplayName("Product Creation")
    class ProductCreation {
        
        @Test
        @DisplayName("Should create product with valid SKU and title")
        void shouldCreateProductWithValidSkuAndTitle() {
            Product product = Product.create(validSku, validTitle);
            
            assertThat(product.getSku()).isEqualTo(validSku);
            assertThat(product.getTitle()).isEqualTo(validTitle);
            assertThat(product.getCreatedAt()).isNotNull();
            assertThat(product.getUpdatedAt()).isNotNull();
            assertThat(product.getVersion()).isEqualTo(0L);
            
            assertThat(product.getDomainEvents()).hasSize(1);
            assertThat(product.getDomainEvents().get(0)).isInstanceOf(ProductCreatedEvent.class);
        }
        
        @Test
        @DisplayName("Should create product with dimensions and attributes")
        void shouldCreateProductWithDimensionsAndAttributes() {
            Dimensions dimensions = createValidDimensions();
            Attributes attributes = Attributes.withoutHazmat();
            
            Product product = Product.create(validSku, validTitle, dimensions, attributes);
            
            assertThat(product.getDimensions()).isEqualTo(dimensions);
            assertThat(product.getAttributes()).isEqualTo(attributes);
        }
        
        @Test
        @DisplayName("Should throw exception when SKU is null")
        void shouldThrowExceptionWhenSkuIsNull() {
            assertThatThrownBy(() -> Product.create(null, validTitle))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("SKU cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception when title is null")
        void shouldThrowExceptionWhenTitleIsNull() {
            assertThatThrownBy(() -> Product.create(validSku, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Title cannot be null");
        }
        
        @Test
        @DisplayName("Should throw exception when title is empty")
        void shouldThrowExceptionWhenTitleIsEmpty() {
            assertThatThrownBy(() -> Product.create(validSku, "   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Title cannot be empty");
        }
    }
    
    @Nested
    @DisplayName("Product Updates")
    class ProductUpdates {
        
        @Test
        @DisplayName("Should update title and generate event")
        void shouldUpdateTitleAndGenerateEvent() {
            Product product = Product.create(validSku, validTitle);
            product.clearDomainEvents();
            
            String newTitle = "Updated Product Title";
            product.updateTitle(newTitle);
            
            assertThat(product.getTitle()).isEqualTo(newTitle);
            assertThat(product.getDomainEvents()).hasSize(1);
            assertThat(product.getDomainEvents().get(0)).isInstanceOf(ProductUpdatedEvent.class);
        }
        
        @Test
        @DisplayName("Should not generate event when title is same")
        void shouldNotGenerateEventWhenTitleIsSame() {
            Product product = Product.create(validSku, validTitle);
            product.clearDomainEvents();
            
            product.updateTitle(validTitle);
            
            assertThat(product.getDomainEvents()).isEmpty();
        }
        
        @Test
        @DisplayName("Should update dimensions and generate event")
        void shouldUpdateDimensionsAndGenerateEvent() {
            Product product = Product.create(validSku, validTitle);
            product.clearDomainEvents();
            
            Dimensions newDimensions = createValidDimensions();
            product.updateDimensions(newDimensions);
            
            assertThat(product.getDimensions()).isEqualTo(newDimensions);
            assertThat(product.getDomainEvents()).hasSize(1);
            assertThat(product.getDomainEvents().get(0)).isInstanceOf(ProductUpdatedEvent.class);
        }
    }
    
    @Nested
    @DisplayName("Business Invariants")
    class BusinessInvariants {
        
        @Test
        @DisplayName("Should enforce equal products have same SKU")
        void shouldEnforceEqualProductsHaveSameSku() {
            Product product1 = Product.create(validSku, validTitle);
            Product product2 = Product.create(validSku, "Different Title");
            
            assertThat(product1).isEqualTo(product2);
            assertThat(product1.hashCode()).isEqualTo(product2.hashCode());
        }
        
        @Test
        @DisplayName("Should enforce products with different SKUs are not equal")
        void shouldEnforceProductsWithDifferentSkusAreNotEqual() {
            Product product1 = Product.create(validSku, validTitle);
            Product product2 = Product.create(SKU.of("DIFFERENT-SKU"), validTitle);
            
            assertThat(product1).isNotEqualTo(product2);
        }
    }
    
    private Dimensions createValidDimensions() {
        DimensionMeasurement length = DimensionMeasurement.of(10.0, DimensionMeasurement.DimensionUnit.INCHES);
        DimensionMeasurement width = DimensionMeasurement.of(8.0, DimensionMeasurement.DimensionUnit.INCHES);
        DimensionMeasurement height = DimensionMeasurement.of(3.0, DimensionMeasurement.DimensionUnit.INCHES);
        WeightMeasurement weight = WeightMeasurement.of(5.0, WeightMeasurement.WeightUnit.POUNDS);
        
        DimensionSet itemDimensions = DimensionSet.of(length, width, height, weight);
        
        DimensionMeasurement packageLength = DimensionMeasurement.of(12.0, DimensionMeasurement.DimensionUnit.INCHES);
        DimensionMeasurement packageWidth = DimensionMeasurement.of(9.0, DimensionMeasurement.DimensionUnit.INCHES);
        DimensionMeasurement packageHeight = DimensionMeasurement.of(4.0, DimensionMeasurement.DimensionUnit.INCHES);
        WeightMeasurement packageWeight = WeightMeasurement.of(6.0, WeightMeasurement.WeightUnit.POUNDS);
        
        DimensionSet packageDimensions = DimensionSet.of(packageLength, packageWidth, packageHeight, packageWeight);
        
        return Dimensions.of(itemDimensions, packageDimensions);
    }
}