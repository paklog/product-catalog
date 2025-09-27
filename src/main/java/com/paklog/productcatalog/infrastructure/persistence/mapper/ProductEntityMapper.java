package com.paklog.productcatalog.infrastructure.persistence.mapper;

import com.paklog.productcatalog.domain.model.*;
import com.paklog.productcatalog.infrastructure.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper {
    
    public ProductEntity toEntity(Product product) {
        if (product == null) {
            return null;
        }

        ProductEntity entity = new ProductEntity();
        entity.setSku(product.getSku().value());
        entity.setTitle(product.getTitle());
        entity.setDimensions(mapDimensions(product.getDimensions()));
        entity.setAttributes(mapAttributes(product.getAttributes()));
        entity.setCreatedAt(product.getCreatedAt());
        entity.setUpdatedAt(product.getUpdatedAt());

        // Only set version if it's not null (for updates)
        if (product.getVersion() != null) {
            entity.setVersion(product.getVersion());
        }

        return entity;
    }
    
    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        
        SKU sku = SKU.of(entity.getSku());
        Dimensions dimensions = mapDimensions(entity.getDimensions());
        Attributes attributes = mapAttributes(entity.getAttributes());
        
        return new Product(sku, entity.getTitle(), dimensions, attributes, 
                          entity.getCreatedAt(), entity.getUpdatedAt(), entity.getVersion());
    }
    
    private ProductEntity.DimensionsEntity mapDimensions(Dimensions dimensions) {
        if (dimensions == null) {
            return null;
        }
        
        return new ProductEntity.DimensionsEntity(
            mapDimensionSet(dimensions.item()),
            mapDimensionSet(dimensions.packageDimensions())
        );
    }
    
    private Dimensions mapDimensions(ProductEntity.DimensionsEntity dimensionsEntity) {
        if (dimensionsEntity == null) {
            return null;
        }
        
        return new Dimensions(
            mapDimensionSet(dimensionsEntity.getItem()),
            mapDimensionSet(dimensionsEntity.getPackageDimensions())
        );
    }
    
    private ProductEntity.DimensionSetEntity mapDimensionSet(DimensionSet dimensionSet) {
        if (dimensionSet == null) {
            return null;
        }
        
        return new ProductEntity.DimensionSetEntity(
            mapDimensionMeasurement(dimensionSet.length()),
            mapDimensionMeasurement(dimensionSet.width()),
            mapDimensionMeasurement(dimensionSet.height()),
            mapWeightMeasurement(dimensionSet.weight())
        );
    }
    
    private DimensionSet mapDimensionSet(ProductEntity.DimensionSetEntity dimensionSetEntity) {
        if (dimensionSetEntity == null) {
            return null;
        }
        
        return new DimensionSet(
            mapDimensionMeasurement(dimensionSetEntity.getLength()),
            mapDimensionMeasurement(dimensionSetEntity.getWidth()),
            mapDimensionMeasurement(dimensionSetEntity.getHeight()),
            mapWeightMeasurement(dimensionSetEntity.getWeight())
        );
    }
    
    private ProductEntity.DimensionMeasurementEntity mapDimensionMeasurement(DimensionMeasurement dimensionMeasurement) {
        if (dimensionMeasurement == null) {
            return null;
        }
        
        return new ProductEntity.DimensionMeasurementEntity(
            dimensionMeasurement.value(),
            dimensionMeasurement.unit().name()
        );
    }
    
    private DimensionMeasurement mapDimensionMeasurement(ProductEntity.DimensionMeasurementEntity dimensionMeasurementEntity) {
        if (dimensionMeasurementEntity == null) {
            return null;
        }
        
        return new DimensionMeasurement(
            dimensionMeasurementEntity.getValue(),
            DimensionMeasurement.DimensionUnit.valueOf(dimensionMeasurementEntity.getUnit())
        );
    }
    
    private ProductEntity.WeightMeasurementEntity mapWeightMeasurement(WeightMeasurement weightMeasurement) {
        if (weightMeasurement == null) {
            return null;
        }
        
        return new ProductEntity.WeightMeasurementEntity(
            weightMeasurement.value(),
            weightMeasurement.unit().name()
        );
    }
    
    private WeightMeasurement mapWeightMeasurement(ProductEntity.WeightMeasurementEntity weightMeasurementEntity) {
        if (weightMeasurementEntity == null) {
            return null;
        }
        
        return new WeightMeasurement(
            weightMeasurementEntity.getValue(),
            WeightMeasurement.WeightUnit.valueOf(weightMeasurementEntity.getUnit())
        );
    }
    
    private ProductEntity.AttributesEntity mapAttributes(Attributes attributes) {
        if (attributes == null) {
            return null;
        }
        
        return new ProductEntity.AttributesEntity(
            mapHazmatInfo(attributes.hazmatInfo())
        );
    }
    
    private Attributes mapAttributes(ProductEntity.AttributesEntity attributesEntity) {
        if (attributesEntity == null) {
            return null;
        }
        
        return new Attributes(
            mapHazmatInfo(attributesEntity.getHazmatInfo())
        );
    }
    
    private ProductEntity.HazmatInfoEntity mapHazmatInfo(HazmatInfo hazmatInfo) {
        if (hazmatInfo == null) {
            return null;
        }
        
        return new ProductEntity.HazmatInfoEntity(
            hazmatInfo.isHazmat(),
            hazmatInfo.unNumber()
        );
    }
    
    private HazmatInfo mapHazmatInfo(ProductEntity.HazmatInfoEntity hazmatInfoEntity) {
        if (hazmatInfoEntity == null) {
            return null;
        }
        
        return new HazmatInfo(
            hazmatInfoEntity.isHazmat(),
            hazmatInfoEntity.getUnNumber()
        );
    }
}