package com.paklog.productcatalog.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "products")
public class ProductEntity {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String sku;
    
    private String title;
    private DimensionsEntity dimensions;
    private AttributesEntity attributes;
    
    private Instant createdAt;
    private Instant updatedAt;
    
    @Version
    private Long version;
    
    public ProductEntity() {}
    
    public ProductEntity(String sku, String title, DimensionsEntity dimensions, 
                        AttributesEntity attributes, Instant createdAt, Instant updatedAt) {
        this.sku = sku;
        this.title = title;
        this.dimensions = dimensions;
        this.attributes = attributes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public DimensionsEntity getDimensions() { return dimensions; }
    public void setDimensions(DimensionsEntity dimensions) { this.dimensions = dimensions; }
    
    public AttributesEntity getAttributes() { return attributes; }
    public void setAttributes(AttributesEntity attributes) { this.attributes = attributes; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    
    public static class DimensionsEntity {
        private DimensionSetEntity item;
        private DimensionSetEntity packageDimensions;
        
        public DimensionsEntity() {}
        
        public DimensionsEntity(DimensionSetEntity item, DimensionSetEntity packageDimensions) {
            this.item = item;
            this.packageDimensions = packageDimensions;
        }
        
        public DimensionSetEntity getItem() { return item; }
        public void setItem(DimensionSetEntity item) { this.item = item; }
        
        public DimensionSetEntity getPackageDimensions() { return packageDimensions; }
        public void setPackageDimensions(DimensionSetEntity packageDimensions) { this.packageDimensions = packageDimensions; }
    }
    
    public static class DimensionSetEntity {
        private DimensionMeasurementEntity length;
        private DimensionMeasurementEntity width;
        private DimensionMeasurementEntity height;
        private WeightMeasurementEntity weight;
        
        public DimensionSetEntity() {}

        public DimensionSetEntity(DimensionMeasurementEntity length, DimensionMeasurementEntity width,
                                DimensionMeasurementEntity height, WeightMeasurementEntity weight) {
            this.length = length;
            this.width = width;
            this.height = height;
            this.weight = weight;
        }
        
        public DimensionMeasurementEntity getLength() { return length; }
        public void setLength(DimensionMeasurementEntity length) { this.length = length; }
        
        public DimensionMeasurementEntity getWidth() { return width; }
        public void setWidth(DimensionMeasurementEntity width) { this.width = width; }
        
        public DimensionMeasurementEntity getHeight() { return height; }
        public void setHeight(DimensionMeasurementEntity height) { this.height = height; }
        
        public WeightMeasurementEntity getWeight() { return weight; }
        public void setWeight(WeightMeasurementEntity weight) { this.weight = weight; }
    }
    
    public static class DimensionMeasurementEntity {
        private BigDecimal value;
        private String unit;
        
        public DimensionMeasurementEntity() {}
        
        public DimensionMeasurementEntity(BigDecimal value, String unit) {
            this.value = value;
            this.unit = unit;
        }
        
        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }
    
    public static class WeightMeasurementEntity {
        private BigDecimal value;
        private String unit;
        
        public WeightMeasurementEntity() {}
        
        public WeightMeasurementEntity(BigDecimal value, String unit) {
            this.value = value;
            this.unit = unit;
        }

        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }

    public static class AttributesEntity {
        private HazmatInfoEntity hazmatInfo;
        
        public AttributesEntity() {}
        
        public AttributesEntity(HazmatInfoEntity hazmatInfo) {
            this.hazmatInfo = hazmatInfo;
        }
        
        public HazmatInfoEntity getHazmatInfo() { return hazmatInfo; }
        public void setHazmatInfo(HazmatInfoEntity hazmatInfo) { this.hazmatInfo = hazmatInfo; }
    }
    
    public static class HazmatInfoEntity {
        private boolean isHazmat;
        private String unNumber;
        
        public HazmatInfoEntity() {}
        
        public HazmatInfoEntity(boolean isHazmat, String unNumber) {
            this.isHazmat = isHazmat;
            this.unNumber = unNumber;
        }
        
        public boolean isHazmat() { return isHazmat; }
        public void setHazmat(boolean hazmat) { isHazmat = hazmat; }
        
        public String getUnNumber() { return unNumber; }
        public void setUnNumber(String unNumber) { this.unNumber = unNumber; }
    }
}
