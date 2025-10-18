# Product Catalog Service

Product catalog service with DDD, CQRS, and event-driven architecture built on Spring Boot, MongoDB, and Kafka.

## Overview

The Product Catalog Service maintains the master product catalog for the Paklog fulfillment platform. This bounded context manages product definitions including SKUs, dimensions, weights, attributes, and hazmat information. It serves as the single source of truth for product data and publishes domain events when product information changes.

## Domain-Driven Design

### Bounded Context
**Product Catalog Management** - Maintains authoritative product information and attributes required for fulfillment operations.

### Core Domain Model

#### Aggregates
- **Product** - Root aggregate representing a unique product with all its attributes

#### Value Objects
- **SKU** - Stock keeping unit identifier
- **Dimensions** - Physical dimensions containing item and package measurements
- **DimensionSet** - Length, width, height with units
- **DimensionMeasurement** - Single dimensional measurement with unit
- **WeightMeasurement** - Weight value with unit
- **HazmatInfo** - Hazardous material information
- **Attributes** - Additional product characteristics

#### Domain Events
- **ProductCreatedEvent** - New product added to catalog
- **ProductUpdatedEvent** - Product information modified
- **ProductDeletedEvent** - Product removed from catalog

### Ubiquitous Language
- **SKU (Stock Keeping Unit)**: Unique identifier for a product
- **Item Dimensions**: Physical size of the product itself
- **Package Dimensions**: Physical size of the product when packaged
- **Hazmat**: Hazardous materials requiring special handling
- **UN Number**: United Nations classification for hazardous materials
- **Product Attributes**: Additional characteristics and metadata

## Architecture & Patterns

### Hexagonal Architecture (Ports and Adapters)

```
src/main/java/com/paklog/productcatalog/
├── domain/                           # Core business logic
│   ├── model/                       # Aggregates and value objects
│   │   ├── Product.java             # Main aggregate root
│   │   ├── SKU.java                 # Value object
│   │   ├── Dimensions.java          # Value object
│   │   └── HazmatInfo.java          # Value object
│   ├── repository/                  # Repository interfaces (ports)
│   ├── event/                       # Domain events
│   └── exception/                   # Domain exceptions
├── application/                      # Use cases & orchestration
│   ├── service/                     # Application services
│   ├── command/                     # Commands
│   ├── query/                       # Queries
│   └── port/                        # Application ports
└── infrastructure/                   # External adapters
    ├── persistence/                 # MongoDB repositories
    ├── messaging/                   # Kafka publishers
    ├── web/                         # REST controllers
    └── config/                      # Configuration
```

### Design Patterns & Principles
- **Hexagonal Architecture** - Clean separation of domain and infrastructure
- **Domain-Driven Design** - Rich domain model with business invariants
- **CQRS** - Separation of command and query responsibilities
- **Event-Driven Architecture** - Integration via domain events
- **Repository Pattern** - Data access abstraction
- **Aggregate Pattern** - Consistency boundaries around Product
- **Value Object Pattern** - Immutable domain concepts
- **SOLID Principles** - Maintainable and extensible code

## Technology Stack

### Core Framework
- **Java 21** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Maven** - Build and dependency management

### Data & Persistence
- **MongoDB** - Document database for aggregates
- **Spring Data MongoDB** - Data access layer
- **Optimistic Locking** - Concurrency control

### Messaging & Events
- **Apache Kafka** - Event streaming platform
- **Spring Kafka** - Kafka integration
- **CloudEvents** - Standardized event format

### API & Documentation
- **Spring Web MVC** - REST API framework
- **SpringDoc OpenAPI 2.2.0** - API documentation and Swagger UI
- **Bean Validation** - Input validation

### Observability
- **Spring Boot Actuator** - Health checks and metrics
- **Micrometer** - Metrics collection
- **Prometheus** - Metrics aggregation
- **Loki Logback Appender** - Log aggregation

### Testing
- **JUnit 5** - Unit testing framework
- **Testcontainers 1.19.3** - Integration testing
- **ArchUnit 1.2.1** - Architecture testing
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Local development environment

## Standards Applied

### Architectural Standards
- ✅ Hexagonal Architecture (Ports and Adapters)
- ✅ Domain-Driven Design tactical patterns
- ✅ CQRS for command/query separation
- ✅ Event-Driven Architecture
- ✅ Microservices architecture
- ✅ RESTful API design

### Code Quality Standards
- ✅ SOLID principles
- ✅ Clean Code practices
- ✅ Comprehensive unit and integration testing
- ✅ Architecture validation with ArchUnit
- ✅ Domain-driven design patterns
- ✅ Immutable value objects
- ✅ Rich domain models with business logic

### Event & Integration Standards
- ✅ CloudEvents specification
- ✅ Event versioning strategy
- ✅ At-least-once delivery semantics
- ✅ Schema evolution support

### Observability Standards
- ✅ Structured logging (JSON)
- ✅ Health check endpoints
- ✅ Prometheus metrics exposition
- ✅ Correlation ID propagation

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+
- Docker & Docker Compose

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/paklog/product-catalog.git
   cd product-catalog
   ```

2. **Start infrastructure services**
   ```bash
   docker-compose up -d mongodb kafka
   ```

3. **Build and run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Verify the service is running**
   ```bash
   curl http://localhost:8082/actuator/health
   ```

### Using Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f product-catalog

# Stop all services
docker-compose down
```

## API Documentation

Once running, access the interactive API documentation:
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8082/api-docs

### Key Endpoints

- `POST /products` - Create new product
- `GET /products/{sku}` - Get product by SKU
- `PUT /products/{sku}` - Update product
- `DELETE /products/{sku}` - Delete product
- `GET /products` - List products with pagination
- `GET /products/search` - Search products

## Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Run architecture tests
mvn test -Dtest=ArchitectureTest

# Run tests with coverage
mvn clean verify jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

## Configuration

Key configuration properties:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/productcatalog
  kafka:
    bootstrap-servers: localhost:9092

product-catalog:
  kafka:
    topics:
      product-events: product.events
```

## Event Integration

### Published Events
- `com.paklog.productcatalog.product.created.v1`
- `com.paklog.productcatalog.product.updated.v1`
- `com.paklog.productcatalog.product.deleted.v1`

### Event Format
All events follow the CloudEvents specification and are published to Kafka.

### Example Event Payload

```json
{
  "specversion": "1.0",
  "type": "com.paklog.productcatalog.product.created.v1",
  "source": "product-catalog-service",
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "time": "2024-01-15T10:30:00Z",
  "datacontenttype": "application/json",
  "data": {
    "sku": "WIDGET-001",
    "title": "Industrial Widget",
    "dimensions": {
      "item": {
        "length": {"value": 10.5, "unit": "INCHES"},
        "width": {"value": 8.0, "unit": "INCHES"},
        "height": {"value": 3.2, "unit": "INCHES"},
        "weight": {"value": 5.0, "unit": "POUNDS"}
      }
    }
  }
}
```

## Monitoring

- **Health**: http://localhost:8082/actuator/health
- **Metrics**: http://localhost:8082/actuator/metrics
- **Prometheus**: http://localhost:8082/actuator/prometheus
- **Info**: http://localhost:8082/actuator/info

## Contributing

1. Follow hexagonal architecture principles
2. Implement domain logic in domain layer
3. Keep infrastructure concerns separate
4. Maintain immutability of value objects
5. Write comprehensive tests for all layers
6. Run architecture tests to validate structure
7. Document domain concepts using ubiquitous language
8. Follow existing code style and conventions

## License

Copyright © 2024 Paklog. All rights reserved.
