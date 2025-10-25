package com.paklog.productcatalog.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paklog.productcatalog.infrastructure.web.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Testcontainers
@Transactional
public class ProductControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    
    public ProductControllerIntegrationTest(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        var productDto = createValidProductDto("TEST-SKU-001", "Test Product");

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("TEST-SKU-001"))
                .andExpect(jsonPath("$.title").value("Test Product"));
    }

    @Test
    void shouldRejectProductWithDuplicateSku() throws Exception {
        var productDto = createValidProductDto("DUPLICATE-SKU", "First Product");
        
        // Create first product
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated());

        // Try to create second product with same SKU
        var duplicateDto = createValidProductDto("DUPLICATE-SKU", "Second Product");
        
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRetrieveProductBySku() throws Exception {
        var productDto = createValidProductDto("RETRIEVE-SKU", "Retrieve Test");
        
        // Create product
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated());

        // Retrieve product
        mockMvc.perform(get("/products/RETRIEVE-SKU"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("RETRIEVE-SKU"))
                .andExpect(jsonPath("$.title").value("Retrieve Test"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentProduct() throws Exception {
        mockMvc.perform(get("/products/NON-EXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListProductsWithPagination() throws Exception {
        // Create multiple products
        for (int i = 1; i <= 25; i++) {
            var productDto = createValidProductDto("LIST-SKU-" + String.format("%03d", i), "Product " + i);
            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productDto)))
                    .andExpect(status().isCreated());
        }

        // Test pagination
        mockMvc.perform(get("/products?offset=0&limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.totalElements").value(25))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.isFirst").value(true))
                .andExpect(jsonPath("$.isLast").value(false));
    }

    @Test
    void shouldUpdateProductSuccessfully() throws Exception {
        var originalDto = createValidProductDto("UPDATE-SKU", "Original Product");
        
        // Create product
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(originalDto)))
                .andExpect(status().isCreated());

        // Update product
        var updatedDto = createValidProductDto("UPDATE-SKU", "Updated Product");
        
        mockMvc.perform(put("/products/UPDATE-SKU")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Product"));
    }

    @Test
    void shouldDeleteProductSuccessfully() throws Exception {
        var productDto = createValidProductDto("DELETE-SKU", "Delete Test");
        
        // Create product
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated());

        // Delete product
        mockMvc.perform(delete("/products/DELETE-SKU"))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/products/DELETE-SKU"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidDimensions() throws Exception {
        var productDto = createInvalidDimensionsProductDto("INVALID-DIMS", "Invalid Dimensions");

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectHazmatWithoutUnNumber() throws Exception {
        var productDto = createHazmatWithoutUnNumberDto("HAZMAT-INVALID", "Invalid Hazmat");

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest());
    }

    private ProductDto createValidProductDto(String sku, String title) {
        var itemDimensions = new ProductDto.DimensionSetDto(
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(5.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(3.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(2.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.WeightMeasurementDto(BigDecimal.valueOf(1.5), ProductDto.WeightUnitDto.POUNDS)
        );

        var packageDimensions = new ProductDto.DimensionSetDto(
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(6.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(4.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(3.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.WeightMeasurementDto(BigDecimal.valueOf(2.0), ProductDto.WeightUnitDto.POUNDS)
        );

        var dimensions = new ProductDto.DimensionsDto(itemDimensions, packageDimensions);
        var hazmatInfo = new ProductDto.HazmatInfoDto(false, null);
        var attributes = new ProductDto.AttributesDto(hazmatInfo);

        return new ProductDto(sku, title, dimensions, attributes);
    }

    private ProductDto createInvalidDimensionsProductDto(String sku, String title) {
        // Item dimensions larger than package dimensions (should be invalid)
        var itemDimensions = new ProductDto.DimensionSetDto(
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(10.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(8.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(6.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.WeightMeasurementDto(BigDecimal.valueOf(5.0), ProductDto.WeightUnitDto.POUNDS)
        );

        var packageDimensions = new ProductDto.DimensionSetDto(
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(5.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(4.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(3.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.WeightMeasurementDto(BigDecimal.valueOf(2.0), ProductDto.WeightUnitDto.POUNDS)
        );

        var dimensions = new ProductDto.DimensionsDto(itemDimensions, packageDimensions);
        var hazmatInfo = new ProductDto.HazmatInfoDto(false, null);
        var attributes = new ProductDto.AttributesDto(hazmatInfo);

        return new ProductDto(sku, title, dimensions, attributes);
    }

    private ProductDto createHazmatWithoutUnNumberDto(String sku, String title) {
        var itemDimensions = new ProductDto.DimensionSetDto(
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(5.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(3.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(2.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.WeightMeasurementDto(BigDecimal.valueOf(1.5), ProductDto.WeightUnitDto.POUNDS)
        );

        var packageDimensions = new ProductDto.DimensionSetDto(
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(6.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(4.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.DimensionMeasurementDto(BigDecimal.valueOf(3.0), ProductDto.DimensionUnitDto.INCHES),
                new ProductDto.WeightMeasurementDto(BigDecimal.valueOf(2.0), ProductDto.WeightUnitDto.POUNDS)
        );

        var dimensions = new ProductDto.DimensionsDto(itemDimensions, packageDimensions);
        // Hazmat is true but unNumber is null (should be invalid)
        var hazmatInfo = new ProductDto.HazmatInfoDto(true, null);
        var attributes = new ProductDto.AttributesDto(hazmatInfo);

        return new ProductDto(sku, title, dimensions, attributes);
    }
}