---
layout: default
title: Home
---

# Product Catalog Service Documentation

Product catalog service with DDD, CQRS, and event-driven architecture built on Spring Boot, MongoDB, and Kafka.

## Overview

The Product Catalog Service maintains the master product catalog for the Paklog fulfillment platform. This bounded context manages product definitions including SKUs, dimensions, weights, attributes, and hazmat information. It serves as the single source of truth for product data.

## Technology Stack

- **Java 21** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **MongoDB** - Document database
- **Apache Kafka** - Event streaming
- **CloudEvents** - Event standard
- **ArchUnit** - Architecture testing

## Key Features

- Master product catalog
- SKU management
- Physical dimensions tracking
- Hazmat information
- Event-driven updates
- CQRS pattern
- Architecture validation

## Domain Model

### Aggregates
- **Product** - Complete product information

### Value Objects
- **SKU** - Stock keeping unit identifier
- **Dimensions** - Item and package measurements
- **DimensionSet** - Length, width, height
- **WeightMeasurement** - Weight with unit
- **HazmatInfo** - Hazardous materials data
- **Attributes** - Additional characteristics

### Domain Events
- **ProductCreatedEvent** - New product added
- **ProductUpdatedEvent** - Product modified
- **ProductDeletedEvent** - Product removed

## Architecture Patterns

- **Hexagonal Architecture** - Clean separation of concerns
- **Domain-Driven Design** - Rich domain model
- **CQRS** - Command/Query separation
- **Event-Driven Architecture** - Async integration
- **Repository Pattern** - Data access abstraction

## API Endpoints

- `POST /products` - Create product
- `GET /products/{sku}` - Get product by SKU
- `PUT /products/{sku}` - Update product
- `DELETE /products/{sku}` - Delete product
- `GET /products` - List products with pagination

## Getting Started

1. Review the main [README](../README.md)
2. Understand the domain model
3. Explore the API documentation
4. Run architecture tests

## Key Concepts

### SKU (Stock Keeping Unit)
Unique identifier for each product in the catalog.

### Dimensions
Physical measurements including:
- **Item Dimensions**: Size of the product itself
- **Package Dimensions**: Size when packaged for shipping

### Hazmat Information
Special handling requirements for hazardous materials including UN numbers.

## Integration Points

### Publishes Events To
- Cartonization (product dimensions)
- Warehouse Operations (product attributes)
- Shipment Transportation (package dimensions)

## Testing

- **Unit Tests**: Domain model validation
- **Integration Tests**: With Testcontainers
- **Architecture Tests**: Enforce architectural rules with ArchUnit

## Contributing

For contribution guidelines, please refer to the main [README](../README.md) in the project root.

## Support

- **GitHub Issues**: [Report bugs or request features](https://github.com/paklog/product-catalog/issues)
- **API Documentation**: Available via Swagger UI when running locally
