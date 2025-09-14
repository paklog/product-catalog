package com.paklog.productcatalog.domain.event;

import com.paklog.productcatalog.domain.model.SKU;

public class ProductUpdatedEvent extends DomainEvent {
    private final SKU sku;
    private final String title;
    
    public ProductUpdatedEvent(SKU sku, String title) {
        super();
        this.sku = sku;
        this.title = title;
    }
    
    public SKU getSku() {
        return sku;
    }
    
    public String getTitle() {
        return title;
    }
    
    @Override
    public String getEventType() {
        return "ProductUpdated";
    }
}