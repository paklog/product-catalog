package com.paklog.productcatalog.domain.event;

import com.paklog.productcatalog.domain.model.SKU;

public class ProductDeletedEvent extends DomainEvent {
    private final SKU sku;
    
    public ProductDeletedEvent(SKU sku) {
        super();
        this.sku = sku;
    }
    
    public SKU getSku() {
        return sku;
    }
    
    @Override
    public String getEventType() {
        return "ProductDeleted";
    }
}