package com.paklog.productcatalog.infrastructure.persistence.repository;

import com.paklog.productcatalog.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductEntityRepository extends MongoRepository<ProductEntity, String> {
    
    Optional<ProductEntity> findBySku(String sku);
    
    boolean existsBySku(String sku);
    
    void deleteBySku(String sku);
}