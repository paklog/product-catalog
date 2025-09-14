package com.paklog.productcatalog.domain.repository;

import com.paklog.productcatalog.domain.model.Product;
import com.paklog.productcatalog.domain.model.SKU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {
    
    Product save(Product product);
    
    Optional<Product> findBySku(SKU sku);
    
    Page<Product> findAll(Pageable pageable);
    
    boolean existsBySku(SKU sku);
    
    void delete(Product product);
    
    void deleteBySku(SKU sku);
    
    long count();
}