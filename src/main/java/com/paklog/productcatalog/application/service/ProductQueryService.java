package com.paklog.productcatalog.application.service;

import com.paklog.productcatalog.application.port.input.GetProductUseCase;
import com.paklog.productcatalog.application.query.GetProductQuery;
import com.paklog.productcatalog.application.query.ListProductsQuery;
import com.paklog.productcatalog.domain.model.Product;
import com.paklog.productcatalog.domain.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@Transactional(readOnly = true)
public class ProductQueryService implements GetProductUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductQueryService.class);
    
    private final ProductRepository productRepository;
    
    public ProductQueryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public Optional<Product> getProduct(GetProductQuery query) {
        logger.debug("Retrieving product with SKU: {}", query.sku());
        return productRepository.findBySku(query.sku());
    }

    @Override
    public Page<Product> listProducts(ListProductsQuery query) {
        logger.debug("Listing products with offset: {} and limit: {}", query.offset(), query.limit());
        return productRepository.findAll(query.toPageable());
    }
}