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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private SKU sku;

        public Builder sku(final SKU sku) { this.sku = sku; return this; }

        public ProductDeletedEvent build() {
            return new ProductDeletedEvent(sku);
        }
    }
}
