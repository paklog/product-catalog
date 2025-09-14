package com.paklog.productcatalog.application.service;

import com.paklog.productcatalog.application.command.CreateProductCommand;
import com.paklog.productcatalog.application.command.DeleteProductCommand;
import com.paklog.productcatalog.application.command.UpdateProductCommand;
import com.paklog.productcatalog.application.port.output.DomainEventPublisher;
import com.paklog.productcatalog.application.query.GetProductQuery;
import com.paklog.productcatalog.application.query.ListProductsQuery;
import com.paklog.productcatalog.domain.model.Product;
import com.paklog.productcatalog.domain.model.SKU;
import com.paklog.productcatalog.domain.repository.ProductRepository;
import com.paklog.productcatalog.shared.exception.ProductAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private DomainEventPublisher eventPublisher;
    
    private ProductService productService;
    
    private final SKU testSku = SKU.of("TEST-SKU-123");
    private final String testTitle = "Test Product";
    
    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, eventPublisher);
    }
    
    @Nested
    @DisplayName("Create Product Use Case")
    class CreateProductUseCase {
        
        @Test
        @DisplayName("Should create product successfully when SKU doesn't exist")
        void shouldCreateProductSuccessfullyWhenSkuDoesntExist() {
            // Given
            CreateProductCommand command = CreateProductCommand.of(testSku, testTitle);
            Product expectedProduct = Product.create(testSku, testTitle);
            
            given(productRepository.existsBySku(testSku)).willReturn(false);
            given(productRepository.save(any(Product.class))).willReturn(expectedProduct);
            
            // When
            Product result = productService.createProduct(command);
            
            // Then
            assertThat(result.getSku()).isEqualTo(testSku);
            assertThat(result.getTitle()).isEqualTo(testTitle);
            
            then(productRepository).should().existsBySku(testSku);
            then(productRepository).should().save(any(Product.class));
            then(eventPublisher).should().publishEvent(any());
        }
        
        @Test
        @DisplayName("Should throw exception when product with SKU already exists")
        void shouldThrowExceptionWhenProductWithSkuAlreadyExists() {
            // Given
            CreateProductCommand command = CreateProductCommand.of(testSku, testTitle);
            given(productRepository.existsBySku(testSku)).willReturn(true);
            
            // When/Then
            assertThatThrownBy(() -> productService.createProduct(command))
                    .isInstanceOf(ProductAlreadyExistsException.class)
                    .hasMessageContaining("Product with SKU " + testSku + " already exists");
            
            then(productRepository).should().existsBySku(testSku);
            then(productRepository).should(never()).save(any(Product.class));
        }
    }
    
    @Nested
    @DisplayName("Get Product Use Case")
    class GetProductUseCase {
        
        @Test
        @DisplayName("Should return product when found")
        void shouldReturnProductWhenFound() {
            // Given
            GetProductQuery query = GetProductQuery.of(testSku);
            Product expectedProduct = Product.create(testSku, testTitle);
            
            given(productRepository.findBySku(testSku)).willReturn(Optional.of(expectedProduct));
            
            // When
            Optional<Product> result = productService.getProduct(query);
            
            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getSku()).isEqualTo(testSku);
            
            then(productRepository).should().findBySku(testSku);
        }
        
        @Test
        @DisplayName("Should return empty when product not found")
        void shouldReturnEmptyWhenProductNotFound() {
            // Given
            GetProductQuery query = GetProductQuery.of(testSku);
            given(productRepository.findBySku(testSku)).willReturn(Optional.empty());
            
            // When
            Optional<Product> result = productService.getProduct(query);
            
            // Then
            assertThat(result).isEmpty();
            then(productRepository).should().findBySku(testSku);
        }
    }
    
    @Nested
    @DisplayName("List Products Use Case")
    class ListProductsUseCase {
        
        @Test
        @DisplayName("Should return paginated products")
        void shouldReturnPaginatedProducts() {
            // Given
            ListProductsQuery query = ListProductsQuery.of(0, 20);
            Product product1 = Product.create(testSku, testTitle);
            Product product2 = Product.create(SKU.of("TEST-SKU-456"), "Another Product");
            
            Page<Product> expectedPage = new PageImpl<>(List.of(product1, product2));
            given(productRepository.findAll(query.toPageable())).willReturn(expectedPage);
            
            // When
            Page<Product> result = productService.listProducts(query);
            
            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).containsExactly(product1, product2);
            
            then(productRepository).should().findAll(query.toPageable());
        }
    }
    
    @Nested
    @DisplayName("Update Product Use Case")
    class UpdateProductUseCase {
        
        @Test
        @DisplayName("Should update product successfully when exists")
        void shouldUpdateProductSuccessfullyWhenExists() {
            // Given
            UpdateProductCommand command = UpdateProductCommand.of(testSku, "Updated Title", null, null);
            Product existingProduct = Product.create(testSku, testTitle);
            
            given(productRepository.findBySku(testSku)).willReturn(Optional.of(existingProduct));
            given(productRepository.save(any(Product.class))).willReturn(existingProduct);
            
            // When
            Optional<Product> result = productService.updateProduct(command);
            
            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).isEqualTo("Updated Title");
            
            then(productRepository).should().findBySku(testSku);
            then(productRepository).should().save(any(Product.class));
        }
        
        @Test
        @DisplayName("Should return empty when product to update doesn't exist")
        void shouldReturnEmptyWhenProductToUpdateDoesntExist() {
            // Given
            UpdateProductCommand command = UpdateProductCommand.of(testSku, "Updated Title", null, null);
            given(productRepository.findBySku(testSku)).willReturn(Optional.empty());
            
            // When
            Optional<Product> result = productService.updateProduct(command);
            
            // Then
            assertThat(result).isEmpty();
            then(productRepository).should().findBySku(testSku);
            then(productRepository).should(never()).save(any(Product.class));
        }
    }
    
    @Nested
    @DisplayName("Delete Product Use Case")
    class DeleteProductUseCase {
        
        @Test
        @DisplayName("Should delete product successfully when exists")
        void shouldDeleteProductSuccessfullyWhenExists() {
            // Given
            DeleteProductCommand command = DeleteProductCommand.of(testSku);
            Product existingProduct = Product.create(testSku, testTitle);
            
            given(productRepository.findBySku(testSku)).willReturn(Optional.of(existingProduct));
            
            // When
            boolean result = productService.deleteProduct(command);
            
            // Then
            assertThat(result).isTrue();
            
            then(productRepository).should().findBySku(testSku);
            then(productRepository).should().delete(existingProduct);
        }
        
        @Test
        @DisplayName("Should return false when product to delete doesn't exist")
        void shouldReturnFalseWhenProductToDeleteDoesntExist() {
            // Given
            DeleteProductCommand command = DeleteProductCommand.of(testSku);
            given(productRepository.findBySku(testSku)).willReturn(Optional.empty());
            
            // When
            boolean result = productService.deleteProduct(command);
            
            // Then
            assertThat(result).isFalse();
            then(productRepository).should().findBySku(testSku);
            then(productRepository).should(never()).delete(any(Product.class));
        }
    }
}