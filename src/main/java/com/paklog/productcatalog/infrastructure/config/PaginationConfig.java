package com.paklog.productcatalog.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "product-catalog.pagination")
public class PaginationConfig {
    
    private int defaultLimit = 20;
    private int maxLimit = 100;
    private int defaultOffset = 0;
    
    public int getDefaultLimit() {
        return defaultLimit;
    }
    
    public void setDefaultLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }
    
    public int getMaxLimit() {
        return maxLimit;
    }
    
    public void setMaxLimit(int maxLimit) {
        this.maxLimit = maxLimit;
    }
    
    public int getDefaultOffset() {
        return defaultOffset;
    }
    
    public void setDefaultOffset(int defaultOffset) {
        this.defaultOffset = defaultOffset;
    }
}