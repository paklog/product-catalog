package com.paklog.productcatalog.application.service;

import com.paklog.productcatalog.application.command.DeleteProductCommand;
import com.paklog.productcatalog.application.port.input.DeleteProductUseCase;
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
public class DeleteProductService implements DeleteProductUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(DeleteProductService.class);
    
    private final ProductRepository productRepository;
    private final DomainEventProcessor eventProcessor;
    
    public DeleteProductService(ProductRepository productRepository, DomainEventProcessor eventProcessor) {
        this.productRepository = productRepository;
        this.eventProcessor = eventProcessor;
    }
    
    @Override
    public boolean deleteProduct(@Valid DeleteProductCommand command) {
        logger.debug("Deleting product with SKU: {}", command.sku());
        
        return productRepository.findBySku(command.sku())
                .map(existingProduct -> {
                    existingProduct.markForDeletion();
                    eventProcessor.processAndClear(existingProduct);
                    
                    productRepository.delete(existingProduct);
                    logger.info("Product deleted successfully with SKU: {}", command.sku());
                    return true;
                })
                .orElse(false);
    }
}