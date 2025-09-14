# Product Catalog Service

A comprehensive Product Catalog service built with **Spring Boot 3**, **MongoDB**, **Apache Kafka**, and **Hexagonal Architecture** following Domain-Driven Design principles.

## Architecture Overview

This service implements a clean, maintainable architecture with clear separation of concerns:

- **Domain Layer**: Core business logic, aggregates, value objects, and domain events
- **Application Layer**: Use cases, commands, queries, and application services  
- **Infrastructure Layer**: External adapters for REST API, MongoDB persistence, and Kafka messaging

## Key Features

- ✅ **Domain-Driven Design** with Product aggregate and business invariants
- ✅ **Hexagonal Architecture** with ports and adapters pattern
- ✅ **CQRS** separation of commands and queries
- ✅ **Event-Driven Architecture** with Kafka domain event publishing
- ✅ **MongoDB** with optimistic locking and proper indexing
- ✅ **Comprehensive Testing** (Unit, Integration, Architecture tests)
- ✅ **OpenAPI 3.0** documentation with Swagger UI
- ✅ **Observability** with custom metrics and health checks
- ✅ **Docker** containerization with multi-stage builds

## Quick Start

### Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven 3.8+

### Running with Docker Compose

```bash
# Start all services (MongoDB, Kafka, Product Catalog)
docker-compose up -d

# View logs
docker-compose logs -f product-catalog

# Stop all services
docker-compose down
```

### Running Locally

```bash
# Start dependencies
docker-compose up -d mongodb kafka

# Run the application
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## API Documentation

Once running, access the interactive API documentation at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Monitoring & Management

- **Health Checks**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics  
- **Prometheus**: http://localhost:8080/actuator/prometheus
- **Kafka UI**: http://localhost:8081
- **MongoDB Express**: http://localhost:8082

## Testing

```bash
# Run all tests
mvn test

# Run only unit tests
mvn test -Dtest="**/*Test"

# Run only integration tests  
mvn test -Dtest="**/*IT"

# Run architecture tests
mvn test -Dtest="ArchitectureTest"
```

## API Examples

### Create a Product

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "WIDGET-123",
    "title": "Industrial Widget",
    "dimensions": {
      "item": {
        "length": {"value": 10.5, "unit": "INCHES"},
        "width": {"value": 8.0, "unit": "INCHES"},
        "height": {"value": 3.2, "unit": "INCHES"},
        "weight": {"value": 5.0, "unit": "POUNDS"}
      },
      "package": {
        "length": {"value": 12.0, "unit": "INCHES"},
        "width": {"value": 9.5, "unit": "INCHES"},
        "height": {"value": 4.0, "unit": "INCHES"},
        "weight": {"value": 5.8, "unit": "POUNDS"}
      }
    },
    "attributes": {
      "hazmat_info": {
        "is_hazmat": false
      }
    }
  }'
```

### Get a Product

```bash
curl http://localhost:8080/products/WIDGET-123
```

### List Products

```bash
curl "http://localhost:8080/products?offset=0&limit=20"
```

## Domain Model

### Core Concepts

- **Product**: Aggregate root representing a unique sellable item
- **SKU**: Unique identifier for products  
- **Dimensions**: Physical measurements (item + package)
- **Attributes**: Additional characteristics (hazmat info, etc.)

### Business Invariants

- Product must have unique, non-empty SKU
- Product must have a title
- Dimensions must have positive values with units
- Item dimensions cannot exceed package dimensions
- Hazmat items require UN number

## Event-Driven Architecture

The service publishes domain events to Kafka:

- **ProductCreated**: When a new product is added
- **ProductUpdated**: When product details change
- **ProductDeleted**: When a product is removed

## Configuration

Key configuration properties in `application.yml`:

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

## Development

### Project Structure

```
src/main/java/com/paklog/productcatalog/
├── domain/                    # Core business logic
│   ├── model/                # Aggregates & Value Objects  
│   ├── repository/           # Repository interfaces
│   ├── service/              # Domain services
│   └── event/                # Domain events
├── application/              # Application layer
│   ├── service/              # Application services
│   ├── command/              # Commands & handlers
│   ├── query/                # Queries & handlers  
│   └── port/                 # Input/Output ports
├── infrastructure/           # External adapters
│   ├── persistence/          # MongoDB implementation
│   ├── messaging/            # Kafka implementation
│   ├── web/                  # REST controllers
│   └── config/               # Configuration classes
└── shared/                   # Cross-cutting concerns
```

### Adding New Features

1. Start with domain modeling in the `domain` package
2. Define application use cases in the `application` package  
3. Implement infrastructure adapters as needed
4. Write comprehensive tests
5. Update API documentation

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)  
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.