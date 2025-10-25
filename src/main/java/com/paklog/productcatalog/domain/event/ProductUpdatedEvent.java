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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private SKU sku;
        private String title;

        public Builder sku(final SKU sku) { this.sku = sku; return this; }
        public Builder title(final String title) { this.title = title; return this; }

        public ProductUpdatedEvent build() {
            return new ProductUpdatedEvent(sku, title);
        }
    }
}
