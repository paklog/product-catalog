package com.paklog.productcatalog.application.service;

import com.paklog.productcatalog.application.command.CreateProductCommand;
import com.paklog.productcatalog.application.port.input.CreateProductUseCase;
import com.paklog.productcatalog.domain.model.Product;
import com.paklog.productcatalog.domain.repository.ProductRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional
public class CreateProductService implements CreateProductUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(CreateProductService.class);
    
    private final ProductRepository productRepository;
    private final DomainEventProcessor eventProcessor;
    
    public CreateProductService(ProductRepository productRepository, DomainEventProcessor eventProcessor) {
        this.productRepository = productRepository;
        this.eventProcessor = eventProcessor;
    }
    
    @Override
    public Product createProduct(@Valid CreateProductCommand command) {
        logger.debug("Creating product with SKU: {}", command.sku());
        
        Product product = Product.create(command.sku(), command.title(), 
                                       command.dimensions(), command.attributes());
        
        // Repository now handles duplicate key exceptions internally
        Product savedProduct = productRepository.save(product);
        eventProcessor.processAndClear(savedProduct);
        
        logger.info("Product created successfully with SKU: {}", command.sku());
        return savedProduct;
    }
}