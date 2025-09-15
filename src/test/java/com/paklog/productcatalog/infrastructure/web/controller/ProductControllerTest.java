package com.paklog.productcatalog.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paklog.productcatalog.application.port.input.CreateProductUseCase;
import com.paklog.productcatalog.application.port.input.DeleteProductUseCase;
import com.paklog.productcatalog.application.port.input.GetProductUseCase;
import com.paklog.productcatalog.application.port.input.UpdateProductUseCase;
import com.paklog.productcatalog.domain.model.Product;
import com.paklog.productcatalog.domain.model.SKU;
import com.paklog.productcatalog.infrastructure.web.dto.ProductDto;
import com.paklog.productcatalog.infrastructure.web.mapper.ProductDtoMapper;
import com.paklog.productcatalog.shared.exception.ProductAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("Product Controller Integration Tests")
class ProductControllerTest {
    
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    
    @MockBean
    private CreateProductUseCase createProductUseCase;
    
    @MockBean
    private GetProductUseCase getProductUseCase;
    
    @MockBean
    private UpdateProductUseCase updateProductUseCase;
    
    @MockBean
    private DeleteProductUseCase deleteProductUseCase;
    
    @MockBean
    private ProductDtoMapper mapper;
    
    private final String testSku = "TEST-SKU-123";
    private final String testTitle = "Test Product";
    
    public ProductControllerTest(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }
    
    @BeforeEach
    void setUp() {
        // Default mapper behavior
        given(mapper.toDomain(any(ProductDto.class)))
                .willAnswer(invocation -> {
                    ProductDto dto = invocation.getArgument(0);
                    return Product.create(SKU.of(dto.sku()), dto.title());
                });
                
        given(mapper.toDto(any(Product.class)))
                .willAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    return new ProductDto(product.getSku().value(), product.getTitle(), null, null);
                });
    }
    
    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() throws Exception {
        // Given
        ProductDto requestDto = new ProductDto(testSku, testTitle, null, null);
        Product createdProduct = Product.create(SKU.of(testSku), testTitle);
        
        given(createProductUseCase.createProduct(any())).willReturn(createdProduct);
        
        // When/Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value(testSku))
                .andExpect(jsonPath("$.title").value(testTitle));
    }
    
    @Test
    @DisplayName("Should return conflict when product already exists")
    void shouldReturnConflictWhenProductAlreadyExists() throws Exception {
        // Given
        ProductDto requestDto = new ProductDto(testSku, testTitle, null, null);
        
        given(createProductUseCase.createProduct(any()))
                .willThrow(new ProductAlreadyExistsException("Product with SKU " + testSku + " already exists"));
        
        // When/Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Product with SKU " + testSku + " already exists"));
    }
    
    @Test
    @DisplayName("Should get product by SKU successfully")
    void shouldGetProductBySkuSuccessfully() throws Exception {
        // Given
        Product product = Product.create(SKU.of(testSku), testTitle);
        given(getProductUseCase.getProduct(any())).willReturn(Optional.of(product));
        
        // When/Then
        mockMvc.perform(get("/products/{sku}", testSku))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value(testSku))
                .andExpect(jsonPath("$.title").value(testTitle));
    }
    
    @Test
    @DisplayName("Should return not found when product doesn't exist")
    void shouldReturnNotFoundWhenProductDoesntExist() throws Exception {
        // Given
        given(getProductUseCase.getProduct(any())).willReturn(Optional.empty());
        
        // When/Then
        mockMvc.perform(get("/products/{sku}", testSku))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should list products with pagination")
    void shouldListProductsWithPagination() throws Exception {
        // Given
        Product product1 = Product.create(SKU.of(testSku), testTitle);
        Product product2 = Product.create(SKU.of("TEST-SKU-456"), "Another Product");
        
        var page = new PageImpl<>(List.of(product1, product2), PageRequest.of(0, 20), 2);
        given(getProductUseCase.listProducts(any())).willReturn(page);
        
        // When/Then
        mockMvc.perform(get("/products")
                .param("offset", "0")
                .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
    
    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() throws Exception {
        // Given
        ProductDto requestDto = new ProductDto(testSku, "Updated Title", null, null);
        Product updatedProduct = Product.create(SKU.of(testSku), "Updated Title");
        
        given(updateProductUseCase.updateProduct(any())).willReturn(Optional.of(updatedProduct));
        
        // When/Then
        mockMvc.perform(put("/products/{sku}", testSku)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value(testSku))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }
    
    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() throws Exception {
        // Given
        given(deleteProductUseCase.deleteProduct(any())).willReturn(true);
        
        // When/Then
        mockMvc.perform(delete("/products/{sku}", testSku))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @DisplayName("Should return not found when deleting non-existent product")
    void shouldReturnNotFoundWhenDeletingNonExistentProduct() throws Exception {
        // Given
        given(deleteProductUseCase.deleteProduct(any())).willReturn(false);
        
        // When/Then
        mockMvc.perform(delete("/products/{sku}", testSku))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should validate request body and return bad request")
    void shouldValidateRequestBodyAndReturnBadRequest() throws Exception {
        // Given
        ProductDto invalidDto = new ProductDto("", "", null, null);
        
        // When/Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}