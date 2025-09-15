package com.paklog.productcatalog.infrastructure.web.mapper;

import com.paklog.productcatalog.domain.model.*;
import com.paklog.productcatalog.infrastructure.web.dto.ProductDto;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper {
    
    public ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }
        
        return new ProductDto(
            product.getSku().value(),
            product.getTitle(),
            mapDimensions(product.getDimensions()),
            mapAttributes(product.getAttributes())
        );
    }
    
    public Product toDomain(ProductDto dto) {
        if (dto == null) {
            return null;
        }
        
        SKU sku = SKU.of(dto.sku());
        Dimensions dimensions = mapDimensions(dto.dimensions());
        Attributes attributes = mapAttributes(dto.attributes());
        
        return Product.create(sku, dto.title(), dimensions, attributes);
    }
    
    private ProductDto.DimensionsDto mapDimensions(Dimensions dimensions) {
        if (dimensions == null) {
            return null;
        }
        
        return new ProductDto.DimensionsDto(
            mapDimensionSet(dimensions.item()),
            mapDimensionSet(dimensions.packageDimensions())
        );
    }
    
    private Dimensions mapDimensions(ProductDto.DimensionsDto dimensionsDto) {
        if (dimensionsDto == null) {
            return null;
        }
        
        return new Dimensions(
            mapDimensionSet(dimensionsDto.item()),
            mapDimensionSet(dimensionsDto.packageDimensions())
        );
    }
    
    private ProductDto.DimensionSetDto mapDimensionSet(DimensionSet dimensionSet) {
        if (dimensionSet == null) {
            return null;
        }
        
        return new ProductDto.DimensionSetDto(
            mapDimensionMeasurement(dimensionSet.length()),
            mapDimensionMeasurement(dimensionSet.width()),
            mapDimensionMeasurement(dimensionSet.height()),
            mapWeightMeasurement(dimensionSet.weight())
        );
    }
    
    private DimensionSet mapDimensionSet(ProductDto.DimensionSetDto dimensionSetDto) {
        if (dimensionSetDto == null) {
            return null;
        }
        
        return new DimensionSet(
            mapDimensionMeasurement(dimensionSetDto.length()),
            mapDimensionMeasurement(dimensionSetDto.width()),
            mapDimensionMeasurement(dimensionSetDto.height()),
            mapWeightMeasurement(dimensionSetDto.weight())
        );
    }
    
    private ProductDto.DimensionMeasurementDto mapDimensionMeasurement(DimensionMeasurement dimensionMeasurement) {
        if (dimensionMeasurement == null) {
            return null;
        }
        
        return new ProductDto.DimensionMeasurementDto(
            dimensionMeasurement.value(),
            ProductDto.DimensionUnitDto.valueOf(dimensionMeasurement.unit().name())
        );
    }
    
    private DimensionMeasurement mapDimensionMeasurement(ProductDto.DimensionMeasurementDto dimensionMeasurementDto) {
        if (dimensionMeasurementDto == null) {
            return null;
        }
        
        return new DimensionMeasurement(
            dimensionMeasurementDto.value(),
            DimensionMeasurement.DimensionUnit.valueOf(dimensionMeasurementDto.unit().name())
        );
    }
    
    private ProductDto.WeightMeasurementDto mapWeightMeasurement(WeightMeasurement weightMeasurement) {
        if (weightMeasurement == null) {
            return null;
        }
        
        return new ProductDto.WeightMeasurementDto(
            weightMeasurement.value(),
            ProductDto.WeightUnitDto.valueOf(weightMeasurement.unit().name())
        );
    }
    
    private WeightMeasurement mapWeightMeasurement(ProductDto.WeightMeasurementDto weightMeasurementDto) {
        if (weightMeasurementDto == null) {
            return null;
        }
        
        return new WeightMeasurement(
            weightMeasurementDto.value(),
            WeightMeasurement.WeightUnit.valueOf(weightMeasurementDto.unit().name())
        );
    }
    
    private ProductDto.AttributesDto mapAttributes(Attributes attributes) {
        if (attributes == null) {
            return null;
        }
        
        return new ProductDto.AttributesDto(
            mapHazmatInfo(attributes.hazmatInfo())
        );
    }
    
    private Attributes mapAttributes(ProductDto.AttributesDto attributesDto) {
        if (attributesDto == null) {
            return null;
        }
        
        return new Attributes(
            mapHazmatInfo(attributesDto.hazmatInfo())
        );
    }
    
    private ProductDto.HazmatInfoDto mapHazmatInfo(HazmatInfo hazmatInfo) {
        if (hazmatInfo == null) {
            return null;
        }
        
        return new ProductDto.HazmatInfoDto(
            hazmatInfo.isHazmat(),
            hazmatInfo.unNumber()
        );
    }
    
    private HazmatInfo mapHazmatInfo(ProductDto.HazmatInfoDto hazmatInfoDto) {
        if (hazmatInfoDto == null) {
            return null;
        }
        
        return new HazmatInfo(
            hazmatInfoDto.isHazmat(),
            hazmatInfoDto.unNumber()
        );
    }
}