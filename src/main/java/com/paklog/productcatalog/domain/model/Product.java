package com.paklog.productcatalog.domain.model;

import com.paklog.productcatalog.domain.event.DomainEvent;
import com.paklog.productcatalog.domain.event.ProductCreatedEvent;
import com.paklog.productcatalog.domain.event.ProductDeletedEvent;
import com.paklog.productcatalog.domain.event.ProductUpdatedEvent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Product {
    @NotNull @Valid
    private final SKU sku;
    
    @NotBlank
    private String title;
    
    @Valid
    private Dimensions dimensions;
    
    @Valid
    private Attributes attributes;
    
    private final Instant createdAt;
    private Instant updatedAt;
    private Long version;
    
    private List<DomainEvent> domainEvents = new ArrayList<>();
    
    public Product(SKU sku, String title, Dimensions dimensions, Attributes attributes, Instant createdAt, Instant updatedAt, Long version) {
        this.sku = sku;
        this.title = title;
        this.dimensions = dimensions;
        this.attributes = attributes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
        this.domainEvents = new java.util.ArrayList<>();
    }

    private Product(SKU sku, String title, Dimensions dimensions, Attributes attributes) {
        this.sku = Objects.requireNonNull(sku, "SKU cannot be null");
        this.title = validateTitle(title);
        this.dimensions = dimensions;
        this.attributes = attributes != null ? attributes : Attributes.withoutHazmat();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.version = 0L;

        this.domainEvents.add(new ProductCreatedEvent(this.sku, this.title));
    }
    
    public static Product create(SKU sku, String title, Dimensions dimensions, Attributes attributes) {
        return new Product(sku, title, dimensions, attributes);
    }
    
    public static Product create(SKU sku, String title) {
        return new Product(sku, title, null, null);
    }
    
    public void updateTitle(String newTitle) {
        String validatedTitle = validateTitle(newTitle);
        if (!this.title.equals(validatedTitle)) {
            this.title = validatedTitle;
            this.updatedAt = Instant.now();
            this.domainEvents.add(new ProductUpdatedEvent(this.sku, this.title));
        }
    }
    
    public void updateDimensions(Dimensions newDimensions) {
        if (!Objects.equals(this.dimensions, newDimensions)) {
            this.dimensions = newDimensions;
            this.updatedAt = Instant.now();
            this.domainEvents.add(new ProductUpdatedEvent(this.sku, this.title));
        }
    }
    
    public void updateAttributes(Attributes newAttributes) {
        if (!Objects.equals(this.attributes, newAttributes)) {
            this.attributes = newAttributes != null ? newAttributes : Attributes.withoutHazmat();
            this.updatedAt = Instant.now();
            this.domainEvents.add(new ProductUpdatedEvent(this.sku, this.title));
        }
    }
    
    public void markForDeletion() {
        this.domainEvents.add(new ProductDeletedEvent(this.sku));
    }
    
    private String validateTitle(String title) {
        Objects.requireNonNull(title, "Title cannot be null");
        String trimmed = title.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        return trimmed;
    }
    
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
    
    public SKU getSku() {
        return sku;
    }
    
    public String getTitle() {
        return title;
    }
    
    public Dimensions getDimensions() {
        return dimensions;
    }
    
    public Attributes getAttributes() {
        return attributes;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(sku, product.sku);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "sku=" + sku +
                ", title='" + title + '\'' +
                ", version=" + version +
                '}';
    }
}