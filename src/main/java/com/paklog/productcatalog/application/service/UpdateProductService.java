package com.paklog.productcatalog.application.service;

import com.paklog.productcatalog.application.command.PatchProductCommand;
import com.paklog.productcatalog.application.command.UpdateProductCommand;
import com.paklog.productcatalog.application.port.input.UpdateProductUseCase;
import com.paklog.productcatalog.domain.model.Product;
import com.paklog.productcatalog.domain.repository.ProductRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@Transactional
public class UpdateProductService implements UpdateProductUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(UpdateProductService.class);
    
    private final ProductRepository productRepository;
    private final DomainEventProcessor eventProcessor;
    
    public UpdateProductService(ProductRepository productRepository, DomainEventProcessor eventProcessor) {
        this.productRepository = productRepository;
        this.eventProcessor = eventProcessor;
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
                    eventProcessor.processAndClear(savedProduct);
                    
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
                    eventProcessor.processAndClear(savedProduct);
                    
                    logger.info("Product patched successfully with SKU: {}", command.sku());
                    return savedProduct;
                });
    }
}