package com.paklog.productcatalog.infrastructure.persistence.repository;

import com.paklog.productcatalog.domain.model.Product;
import com.paklog.productcatalog.domain.model.SKU;
import com.paklog.productcatalog.domain.repository.ProductRepository;
import com.paklog.productcatalog.infrastructure.persistence.mapper.ProductEntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MongoProductRepository implements ProductRepository {

    private static final Logger logger = LoggerFactory.getLogger(MongoProductRepository.class);

    private final ProductEntityRepository entityRepository;
    private final ProductEntityMapper mapper;

    public MongoProductRepository(ProductEntityRepository entityRepository,
                                ProductEntityMapper mapper) {
        this.entityRepository = entityRepository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        logger.debug("Saving product with SKU: {}", product.getSku());

        try {
            var entity = mapper.toEntity(product);

            // For updates, preserve the existing entity's MongoDB ID
            if (product.getVersion() != null) {
                entityRepository.findBySku(product.getSku().value())
                    .ifPresent(existingEntity -> entity.setId(existingEntity.getId()));
            }

            var savedEntity = entityRepository.save(entity);
            var savedProduct = mapper.toDomain(savedEntity);
            savedProduct.setVersion(savedEntity.getVersion());

            logger.debug("Product saved successfully with SKU: {}, Version: {}",
                        savedProduct.getSku(), savedProduct.getVersion());

            return savedProduct;
        } catch (org.springframework.dao.DuplicateKeyException e) {
            logger.warn("Attempted to save product with duplicate SKU: {}", product.getSku());
            throw new com.paklog.productcatalog.shared.exception.ProductAlreadyExistsException(
                "Product with SKU " + product.getSku() + " already exists");
        }
    }
    
    @Override
    public Optional<Product> findBySku(SKU sku) {
        logger.debug("Finding product by SKU: {}", sku);
        
        return entityRepository.findBySku(sku.value())
                .map(entity -> {
                    var product = mapper.toDomain(entity);
                    product.setVersion(entity.getVersion());
                    return product;
                });
    }
    
    @Override
    public Page<Product> findAll(Pageable pageable) {
        logger.debug("Finding all products with pageable: {}", pageable);
        
        return entityRepository.findAll(pageable)
                .map(entity -> {
                    var product = mapper.toDomain(entity);
                    product.setVersion(entity.getVersion());
                    return product;
                });
    }
    
    @Override
    public boolean existsBySku(SKU sku) {
        logger.debug("Checking existence of product with SKU: {}", sku);
        return entityRepository.existsBySku(sku.value());
    }
    
    @Override
    public void delete(Product product) {
        logger.debug("Deleting product with SKU: {}", product.getSku());
        entityRepository.deleteBySku(product.getSku().value());
    }
    
    @Override
    public void deleteBySku(SKU sku) {
        logger.debug("Deleting product by SKU: {}", sku);
        entityRepository.deleteBySku(sku.value());
    }
    
    @Override
    public long count() {
        return entityRepository.count();
    }
}