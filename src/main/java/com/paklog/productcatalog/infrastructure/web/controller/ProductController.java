package com.paklog.productcatalog.infrastructure.web.controller;

import com.paklog.productcatalog.application.command.CreateProductCommand;
import com.paklog.productcatalog.application.command.DeleteProductCommand;
import com.paklog.productcatalog.application.command.PatchProductCommand;
import com.paklog.productcatalog.application.command.UpdateProductCommand;
import com.paklog.productcatalog.application.port.input.CreateProductUseCase;
import com.paklog.productcatalog.application.port.input.DeleteProductUseCase;
import com.paklog.productcatalog.application.port.input.GetProductUseCase;
import com.paklog.productcatalog.application.port.input.UpdateProductUseCase;
import com.paklog.productcatalog.application.query.GetProductQuery;
import com.paklog.productcatalog.application.query.ListProductsQuery;
import com.paklog.productcatalog.domain.model.SKU;
import com.paklog.productcatalog.infrastructure.web.dto.ErrorDto;
import com.paklog.productcatalog.infrastructure.web.dto.ProductDto;
import com.paklog.productcatalog.infrastructure.web.dto.ProductPageDto;
import com.paklog.productcatalog.infrastructure.web.mapper.ProductDtoMapper;
import com.paklog.productcatalog.shared.exception.ProductAlreadyExistsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Operations related to the Product Catalog")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final ProductDtoMapper mapper;
    private final com.paklog.productcatalog.infrastructure.config.PaginationConfig paginationConfig;
    
    public ProductController(CreateProductUseCase createProductUseCase,
                           GetProductUseCase getProductUseCase,
                           UpdateProductUseCase updateProductUseCase,
                           DeleteProductUseCase deleteProductUseCase,
                           ProductDtoMapper mapper,
                           com.paklog.productcatalog.infrastructure.config.PaginationConfig paginationConfig) {
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.mapper = mapper;
        this.paginationConfig = paginationConfig;
    }
    
    @PostMapping
    @Operation(
        summary = "Create a new product",
        description = "Adds a new product with its dimensions and attributes to the catalog. The SKU must be unique."
    )
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "409", description = "Product with SKU already exists",
                content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        logger.info("Creating product with SKU: {}", productDto.sku());
        
        try {
            var product = mapper.toDomain(productDto);
            var command = CreateProductCommand.of(
                product.getSku(), 
                product.getTitle(), 
                product.getDimensions(), 
                product.getAttributes()
            );
            
            var createdProduct = createProductUseCase.createProduct(command);
            var responseDto = mapper.toDto(createdProduct);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
            
        } catch (ProductAlreadyExistsException e) {
            logger.warn("Product creation failed: {}", e.getMessage());
            throw e;
        }
    }
    
    @GetMapping
    @Operation(
        summary = "List all products",
        description = "Retrieves a paginated list of all products in the catalog."
    )
    @ApiResponse(responseCode = "200", description = "A paged array of products")
    public ResponseEntity<ProductPageDto> listProducts(
        @Parameter(description = "The number of items to skip for pagination")
        @RequestParam(required = false) @Min(0) Integer offset,
        
        @Parameter(description = "The number of items to return")
        @RequestParam(required = false) @Min(1) Integer limit
    ) {
        // Use configuration defaults if not provided
        int actualOffset = offset != null ? offset : paginationConfig.getDefaultOffset();
        int actualLimit = limit != null ? Math.min(limit, paginationConfig.getMaxLimit()) : paginationConfig.getDefaultLimit();
        logger.debug("Listing products with offset: {} and limit: {}", actualOffset, actualLimit);
        
        var query = ListProductsQuery.of(actualOffset, actualLimit);
        var products = getProductUseCase.listProducts(query);
        var productDtos = products.map(mapper::toDto).getContent();
        
        var response = new ProductPageDto(
            productDtos,
            products.getTotalPages(),
            products.getTotalElements(),
            products.getNumber(),
            products.getSize(),
            products.isFirst(),
            products.isLast()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{sku}")
    @Operation(
        summary = "Get product by SKU",
        description = "Retrieves a single product by its unique Stock Keeping Unit (SKU)."
    )
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "404", description = "Product not found",
                content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    public ResponseEntity<ProductDto> getProductBySku(
        @Parameter(description = "The unique SKU of the product", required = true)
        @PathVariable String sku
    ) {
        logger.debug("Getting product by SKU: {}", sku);
        
        var query = GetProductQuery.of(SKU.of(sku));
        return getProductUseCase.getProduct(query)
                .map(product -> ResponseEntity.ok(mapper.toDto(product)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{sku}")
    @Operation(
        summary = "Update a product (Full Replace)",
        description = "Replaces the entire product resource with the provided data. All fields are required."
    )
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductDto> updateProduct(
        @Parameter(description = "The unique SKU of the product to update", required = true)
        @PathVariable String sku,
        
        @Valid @RequestBody ProductDto productDto
    ) {
        logger.info("Updating product with SKU: {}", sku);
        
        var product = mapper.toDomain(productDto);
        var command = UpdateProductCommand.of(
            SKU.of(sku), 
            product.getTitle(), 
            product.getDimensions(), 
            product.getAttributes()
        );
        
        return updateProductUseCase.updateProduct(command)
                .map(updatedProduct -> ResponseEntity.ok(mapper.toDto(updatedProduct)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{sku}")
    @Operation(
        summary = "Partially update a product",
        description = "Updates one or more fields of an existing product. Fields not included in the request body will not be changed."
    )
    @ApiResponse(responseCode = "200", description = "Product partially updated")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductDto> patchProduct(
        @Parameter(description = "The unique SKU of the product to update", required = true)
        @PathVariable String sku,
        
        @Valid @RequestBody ProductDto productDto
    ) {
        logger.info("Patching product with SKU: {}", sku);
        
        var product = mapper.toDomain(productDto);
        var command = PatchProductCommand.of(
            SKU.of(sku),
            Optional.ofNullable(product.getTitle()),
            Optional.ofNullable(product.getDimensions()),
            Optional.ofNullable(product.getAttributes())
        );
        
        return updateProductUseCase.patchProduct(command)
                .map(updatedProduct -> ResponseEntity.ok(mapper.toDto(updatedProduct)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{sku}")
    @Operation(
        summary = "Delete a product",
        description = "Deletes a product from the catalog by its SKU."
    )
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<Void> deleteProduct(
        @Parameter(description = "The unique SKU of the product to delete", required = true)
        @PathVariable String sku
    ) {
        logger.info("Deleting product with SKU: {}", sku);
        
        var command = DeleteProductCommand.of(SKU.of(sku));
        boolean deleted = deleteProductUseCase.deleteProduct(command);
        
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}