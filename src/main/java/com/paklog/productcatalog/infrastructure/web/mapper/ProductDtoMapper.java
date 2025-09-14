package com.paklog.productcatalog.infrastructure.web.mapper;

import com.paklog.productcatalog.domain.model.*;
import com.paklog.productcatalog.infrastructure.web.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductDtoMapper {
    
    @Mapping(source = "sku.value", target = "sku")
    ProductDto toDto(Product product);
    
    @Mapping(source = "sku", target = "sku", qualifiedByName = "stringToSku")
    Product toDomain(ProductDto dto);
    
    @org.mapstruct.Named("stringToSku")
    default SKU stringToSku(String sku) {
        return sku != null ? SKU.of(sku) : null;
    }
    
    @Mapping(source = "packageDimensions", target = "package")
    ProductDto.DimensionsDto map(Dimensions dimensions);
    
    @Mapping(source = "package", target = "packageDimensions")
    Dimensions map(ProductDto.DimensionsDto dimensionsDto);
    
    ProductDto.DimensionSetDto map(DimensionSet dimensionSet);
    DimensionSet map(ProductDto.DimensionSetDto dimensionSetDto);
    
    @Mapping(source = "unit", target = "unit", qualifiedByName = "dimensionUnitToDto")
    ProductDto.DimensionMeasurementDto map(DimensionMeasurement dimensionMeasurement);
    
    @Mapping(source = "unit", target = "unit", qualifiedByName = "dtoToDimensionUnit")
    DimensionMeasurement map(ProductDto.DimensionMeasurementDto dimensionMeasurementDto);
    
    @Mapping(source = "unit", target = "unit", qualifiedByName = "weightUnitToDto")
    ProductDto.WeightMeasurementDto map(WeightMeasurement weightMeasurement);
    
    @Mapping(source = "unit", target = "unit", qualifiedByName = "dtoToWeightUnit")
    WeightMeasurement map(ProductDto.WeightMeasurementDto weightMeasurementDto);
    
    ProductDto.AttributesDto map(Attributes attributes);
    Attributes map(ProductDto.AttributesDto attributesDto);
    
    ProductDto.HazmatInfoDto map(HazmatInfo hazmatInfo);
    HazmatInfo map(ProductDto.HazmatInfoDto hazmatInfoDto);
    
    @org.mapstruct.Named("dimensionUnitToDto")
    default ProductDto.DimensionUnitDto dimensionUnitToDto(DimensionMeasurement.DimensionUnit unit) {
        return unit != null ? ProductDto.DimensionUnitDto.valueOf(unit.name()) : null;
    }
    
    @org.mapstruct.Named("dtoToDimensionUnit")
    default DimensionMeasurement.DimensionUnit dtoToDimensionUnit(ProductDto.DimensionUnitDto unit) {
        return unit != null ? DimensionMeasurement.DimensionUnit.valueOf(unit.name()) : null;
    }
    
    @org.mapstruct.Named("weightUnitToDto")
    default ProductDto.WeightUnitDto weightUnitToDto(WeightMeasurement.WeightUnit unit) {
        return unit != null ? ProductDto.WeightUnitDto.valueOf(unit.name()) : null;
    }
    
    @org.mapstruct.Named("dtoToWeightUnit")
    default WeightMeasurement.WeightUnit dtoToWeightUnit(ProductDto.WeightUnitDto unit) {
        return unit != null ? WeightMeasurement.WeightUnit.valueOf(unit.name()) : null;
    }
}