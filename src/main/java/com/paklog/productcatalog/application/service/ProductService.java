package com.paklog.productcatalog.application.service;

import com.paklog.productcatalog.application.command.CreateProductCommand;
import com.paklog.productcatalog.application.command.DeleteProductCommand;
import com.paklog.productcatalog.application.command.PatchProductCommand;
import com.paklog.productcatalog.application.command.UpdateProductCommand;
import com.paklog.productcatalog.application.port.input.CreateProductUseCase;
import com.paklog.productcatalog.application.port.input.DeleteProductUseCase;
import com.paklog.productcatalog.application.port.input.GetProductUseCase;
import com.paklog.productcatalog.application.port.input.UpdateProductUseCase;
import com.paklog.productcatalog.application.port.output.DomainEventPublisher;
import com.paklog.productcatalog.application.query.GetProductQuery;
import com.paklog.productcatalog.application.query.ListProductsQuery;
import com.paklog.productcatalog.domain.event.DomainEvent;
import com.paklog.productcatalog.domain.model.Product;
import com.paklog.productcatalog.domain.repository.ProductRepository;
import com.paklog.productcatalog.shared.exception.ProductAlreadyExistsException;
import com.paklog.productcatalog.shared.exception.ProductNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@Transactional
public class ProductService implements CreateProductUseCase, GetProductUseCase, 
                                     UpdateProductUseCase, DeleteProductUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    private final ProductRepository productRepository;
    private final DomainEventPublisher eventPublisher;
    
    public ProductService(ProductRepository productRepository, DomainEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Product createProduct(@Valid CreateProductCommand command) {
        logger.debug("Creating product with SKU: {}", command.sku());
        
        if (productRepository.existsBySku(command.sku())) {
            throw new ProductAlreadyExistsException("Product with SKU " + command.sku() + " already exists");
        }
        
        Product product = Product.create(command.sku(), command.title(), 
                                       command.dimensions(), command.attributes());
        
        Product savedProduct = productRepository.save(product);
        publishDomainEvents(savedProduct);
        
        logger.info("Product created successfully with SKU: {}", command.sku());
        return savedProduct;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProduct(@Valid GetProductQuery query) {
        logger.debug("Retrieving product with SKU: {}", query.sku());
        return productRepository.findBySku(query.sku());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> listProducts(@Valid ListProductsQuery query) {
        logger.debug("Listing products with offset: {} and limit: {}", query.offset(), query.limit());
        return productRepository.findAll(query.toPageable());
    }
    
    @Override
    public Optional<Product> updateProduct(@Valid UpdateProductCommand command) {
        logger.debug("Updating product with SKU: {}", command.sku());
        
        return productRepository.findBySku(command.sku())
                .map(existingProduct -> {
                    existingProduct.updateTitle(command.title());
                    existingProduct.updateDimensions(command.dimensions());
                    existingProduct.updateAttributes(command.attributes());
                    
                    Product savedProduct = productRepository.save(existingProduct);
                    publishDomainEvents(savedProduct);
                    
                    logger.info("Product updated successfully with SKU: {}", command.sku());
                    return savedProduct;
                });
    }
    
    @Override
    public Optional<Product> patchProduct(@Valid PatchProductCommand command) {
        logger.debug("Patching product with SKU: {}", command.sku());
        
        return productRepository.findBySku(command.sku())
                .map(existingProduct -> {
                    command.title().ifPresent(existingProduct::updateTitle);
                    command.dimensions().ifPresent(existingProduct::updateDimensions);
                    command.attributes().ifPresent(existingProduct::updateAttributes);
                    
                    Product savedProduct = productRepository.save(existingProduct);
                    publishDomainEvents(savedProduct);
                    
                    logger.info("Product patched successfully with SKU: {}", command.sku());
                    return savedProduct;
                });
    }
    
    @Override
    public boolean deleteProduct(@Valid DeleteProductCommand command) {
        logger.debug("Deleting product with SKU: {}", command.sku());
        
        return productRepository.findBySku(command.sku())
                .map(existingProduct -> {
                    existingProduct.markForDeletion();
                    publishDomainEvents(existingProduct);
                    
                    productRepository.delete(existingProduct);
                    logger.info("Product deleted successfully with SKU: {}", command.sku());
                    return true;
                })
                .orElse(false);
    }
    
    private void publishDomainEvents(Product product) {
        product.getDomainEvents().forEach(this::publishEvent);
        product.clearDomainEvents();
    }
    
    private void publishEvent(DomainEvent event) {
        try {
            eventPublisher.publish(event);
            logger.debug("Published domain event: {} with ID: {}", event.getEventType(), event.getEventId());
        } catch (Exception e) {
            logger.error("Failed to publish domain event: {} with ID: {}", 
                        event.getEventType(), event.getEventId(), e);
            throw e;
        }
    }
}