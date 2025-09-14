package com.paklog.productcatalog.infrastructure.persistence.mapper;

import com.paklog.productcatalog.domain.model.*;
import com.paklog.productcatalog.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductEntityMapper {
    
    @Mapping(source = "sku.value", target = "sku")
    @Mapping(source = "dimensions", target = "dimensions")
    @Mapping(source = "attributes", target = "attributes")
    ProductEntity toEntity(Product product);
    
    @Mapping(source = "sku", target = "sku", qualifiedByName = "stringToSku")
    @Mapping(source = "dimensions", target = "dimensions")
    @Mapping(source = "attributes", target = "attributes")
    Product toDomain(ProductEntity entity);
    
    @org.mapstruct.Named("stringToSku")
    default SKU stringToSku(String sku) {
        return sku != null ? SKU.of(sku) : null;
    }
    
    ProductEntity.DimensionsEntity map(Dimensions dimensions);
    Dimensions map(ProductEntity.DimensionsEntity dimensionsEntity);
    
    ProductEntity.DimensionSetEntity map(DimensionSet dimensionSet);
    DimensionSet map(ProductEntity.DimensionSetEntity dimensionSetEntity);
    
    @Mapping(source = "unit", target = "unit", qualifiedByName = "dimensionUnitToString")
    ProductEntity.DimensionMeasurementEntity map(DimensionMeasurement dimensionMeasurement);
    
    @Mapping(source = "unit", target = "unit", qualifiedByName = "stringToDimensionUnit")
    DimensionMeasurement map(ProductEntity.DimensionMeasurementEntity dimensionMeasurementEntity);
    
    @Mapping(source = "unit", target = "unit", qualifiedByName = "weightUnitToString")
    ProductEntity.WeightMeasurementEntity map(WeightMeasurement weightMeasurement);
    
    @Mapping(source = "unit", target = "unit", qualifiedByName = "stringToWeightUnit")
    WeightMeasurement map(ProductEntity.WeightMeasurementEntity weightMeasurementEntity);
    
    ProductEntity.AttributesEntity map(Attributes attributes);
    Attributes map(ProductEntity.AttributesEntity attributesEntity);
    
    ProductEntity.HazmatInfoEntity map(HazmatInfo hazmatInfo);
    HazmatInfo map(ProductEntity.HazmatInfoEntity hazmatInfoEntity);
    
    @org.mapstruct.Named("dimensionUnitToString")
    default String dimensionUnitToString(DimensionMeasurement.DimensionUnit unit) {
        return unit != null ? unit.name() : null;
    }
    
    @org.mapstruct.Named("stringToDimensionUnit")
    default DimensionMeasurement.DimensionUnit stringToDimensionUnit(String unit) {
        return unit != null ? DimensionMeasurement.DimensionUnit.valueOf(unit) : null;
    }
    
    @org.mapstruct.Named("weightUnitToString")
    default String weightUnitToString(WeightMeasurement.WeightUnit unit) {
        return unit != null ? unit.name() : null;
    }
    
    @org.mapstruct.Named("stringToWeightUnit")
    default WeightMeasurement.WeightUnit stringToWeightUnit(String unit) {
        return unit != null ? WeightMeasurement.WeightUnit.valueOf(unit) : null;
    }
}