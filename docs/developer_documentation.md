# Developer Documentation: Product Catalog Service

## 1. OVERVIEW

### Purpose and Primary Functionality
The Product Catalog Service is a dedicated microservice responsible for managing product data within a larger system, typically an e-commerce or inventory management platform. Its primary functionality includes:
- **Product Creation:** Adding new products with unique SKUs, titles, dimensions, and attributes.
- **Product Retrieval:** Fetching individual product details by SKU or listing all products with pagination.
- **Product Updates:** Modifying existing product information, either partially or completely.
- **Product Deletion:** Removing products from the catalog.
- **Dimension Management:** Storing and managing physical dimensions (length, width, height, weight) for both the item and its packaging, with support for various units.
- **Attribute Management:** Handling additional product characteristics, including hazardous material information.
- **Event Publishing:** Emitting domain events (e.g., `ProductCreatedEvent`, `ProductUpdatedEvent`, `ProductDeletedEvent`) to a message broker for other services to consume, facilitating an event-driven architecture.

### When to Use This Component vs. Alternatives
This component is ideal when:
- You need a dedicated, scalable, and independently deployable service for product information.
- Your system follows a microservices architecture.
- You require strong domain modeling (Domain-Driven Design) for product data.
- You need to integrate with other services via asynchronous messaging (Kafka).
- You prefer a NoSQL database (MongoDB) for flexible schema and high scalability for product data.

Alternatives might include:
- **Monolithic Application:** Integrating product catalog functionality directly into a larger application. This can lead to tight coupling and reduced scalability.
- **Commercial PIM (Product Information Management) Systems:** Off-the-shelf solutions that offer extensive features but might be overkill for simpler needs or less customizable.
- **Relational Database-backed Service:** Using a relational database instead of MongoDB. This might be preferred for strong transactional consistency across multiple aggregates, but could be less flexible for evolving product schemas.

### Architectural Context
The Product Catalog Service is designed as a microservice adhering to **Domain-Driven Design (DDD)** principles and a **Hexagonal Architecture**.
- **Microservice:** It operates as an independent service, communicating with other services primarily through its REST API and a message broker (Kafka).
- **Domain-Driven Design:** The `Product` is the central Aggregate Root, encapsulating business logic and ensuring data consistency. Value Objects like `Dimensions`, `DimensionSet`, `DimensionMeasurement`, `WeightMeasurement`, `Attributes`, and `HazmatInfo` are used to model specific domain concepts.
- **Hexagonal Architecture (Ports & Adapters):**
    - **Application Layer:** Contains application services that orchestrate domain logic and interact with ports.
    - **Domain Layer:** The core of the application, containing entities, value objects, aggregates, and repositories. It is independent of external technologies.
    - **Infrastructure Layer:** Implements the ports defined in the domain and application layers. This includes adapters for web (REST controllers), persistence (MongoDB), and messaging (Kafka producers/consumers).

This architecture promotes loose coupling, testability, and maintainability by clearly separating concerns and making the domain logic independent of external technologies.

## 2. TECHNICAL SPECIFICATION

### API Reference
The API is defined using OpenAPI 3.0.3 and can be accessed via the `/v3/api-docs` endpoint (or `/swagger-ui.html` for the UI).

**Base URL:** `https://api.example.com/v1` (configurable via `application.yml` or environment variables)

#### `POST /products`
- **Summary:** Create a new product
- **Description:** Adds a new product with its dimensions and attributes to the catalog. The SKU must be unique.
- **Operation ID:** `createProduct`
- **Request Body:**
    - `application/json`:
        - **Schema:** `Product` (see Schemas below)
        - **Example:**
          ```json
          {
            "sku": "EXAMPLE-SKU-123",
            "title": "Industrial Grade Widget",
            "dimensions": {
              "item": {
                "length": { "value": 10.5, "unit": "INCHES" },
                "width": { "value": 8.0, "unit": "INCHES" },
                "height": { "value": 3.2, "unit": "INCHES" },
                "weight": { "value": 5.0, "unit": "POUNDS" }
              },
              "package": {
                "length": { "value": 12.0, "unit": "INCHES" },
                "width": { "value": 9.5, "unit": "INCHES" },
                "height": { "value": 4.0, "unit": "INCHES" },
                "weight": { "value": 5.8, "unit": "POUNDS" }
              }
            },
            "attributes": {
              "hazmatInfo": {
                "isHazmat": true,
                "unNumber": "UN1950"
              }
            }
          }
          ```
- **Responses:**
    - `201 Created`: Product created successfully. Returns the created `Product` object.
    - `400 Bad Request`: Invalid input data.
    - `409 Conflict`: Product with the given SKU already exists.

#### `GET /products`
- **Summary:** List all products
- **Description:** Retrieves a paginated list of all products in the catalog.
- **Operation ID:** `listProducts`
- **Parameters:**
    - `limit` (query, integer, default: 20): The number of items to return.
    - `offset` (query, integer, default: 0): The number of items to skip for pagination.
- **Responses:**
    - `200 OK`: A paged array of `Product` objects.

#### `GET /products/{sku}`
- **Summary:** Get product by SKU
- **Description:** Retrieves a single product by its unique Stock Keeping Unit (SKU).
- **Operation ID:** `getProductBySku`
- **Parameters:**
    - `sku` (path, string, required): The unique SKU of the product.
- **Responses:**
    - `200 OK`: Successful operation. Returns the `Product` object.
    - `404 Not Found`: Product not found.

#### `PUT /products/{sku}`
- **Summary:** Update a product (Full Replace)
- **Description:** Replaces the entire product resource with the provided data. All fields are required.
- **Operation ID:** `updateProduct`
- **Parameters:**
    - `sku` (path, string, required): The unique SKU of the product to update.
- **Request Body:**
    - `application/json`:
        - **Schema:** `Product` (see Schemas below)
- **Responses:**
    - `200 OK`: Product updated successfully. Returns the updated `Product` object.
    - `400 Bad Request`: Invalid input data.
    - `404 Not Found`: Product not found.

#### `PATCH /products/{sku}`
- **Summary:** Partially update a product
- **Description:** Updates one or more fields of an existing product. Fields not included in the request body will not be changed.
- **Operation ID:** `patchProduct`
- **Parameters:**
    - `sku` (path, string, required): The unique SKU of the product to update.
- **Request Body:**
    - `application/json`:
        - **Schema:** `Product` (partial `Product` object with fields to update)
- **Responses:**
    - `200 OK`: Product partially updated. Returns the updated `Product` object.
    - `400 Bad Request`: Invalid input data.
    - `404 Not Found`: Product not found.

#### `DELETE /products/{sku}`
- **Summary:** Delete a product
- **Description:** Deletes a product from the catalog by its SKU.
- **Operation ID:** `deleteProduct`
- **Parameters:**
    - `sku` (path, string, required): The unique SKU of the product to delete.
- **Responses:**
    - `204 No Content`: Product deleted successfully.
    - `404 Not Found`: Product not found.

### Schemas

#### `Product`
- **Type:** `object`
- **Description:** The core Product aggregate root, representing a unique item in the catalog.
- **Required:** `sku`, `title`
- **Properties:**
    - `sku` (string): The unique, seller-defined Stock Keeping Unit. Example: `"EXAMPLE-SKU-123"`
    - `title` (string): The display name of the product. Example: `"Industrial Grade Widget"`
    - `dimensions` (`Dimensions` object): Physical dimensions of the item and its packaging.
    - `attributes` (`Attributes` object): Additional product characteristics and compliance data.

#### `Dimensions`
- **Type:** `object`
- **Description:** A Value Object containing the physical dimensions of the item and its packaging.
- **Properties:**
    - `item` (`DimensionSet` object): Dimensions of the product itself.
    - `package` (`DimensionSet` object): Dimensions of the product in its shippable packaging.

#### `DimensionSet`
- **Type:** `object`
- **Description:** A complete set of measurements for an object.
- **Properties:**
    - `length` (`DimensionMeasurement` object)
    - `width` (`DimensionMeasurement` object)
    - `height` (`DimensionMeasurement` object)
    - `weight` (`WeightMeasurement` object)

#### `DimensionMeasurement`
- **Type:** `object`
- **Description:** A measurement of length, width, or height.
- **Properties:**
    - `value` (number, float): Example: `10.5`
    - `unit` (string, enum): Example: `"INCHES"` (Possible values: "INCHES", "CENTIMETERS", etc.)

#### `WeightMeasurement`
- **Type:** `object`
- **Description:** A measurement of weight.
- **Properties:**
    - `value` (number, float): Example: `5.8`
    - `unit` (string, enum): Example: `"POUNDS"` (Possible values: "POUNDS", "KILOGRAMS", etc.)

#### `Attributes`
- **Type:** `object`
- **Description:** A Value Object for storing additional product characteristics and compliance data.
- **Properties:**
    - `hazmatInfo` (`HazmatInfo` object)

#### `HazmatInfo`
- **Type:** `object`
- **Description:** Information related to hazardous material classification.
- **Properties:**
    - `isHazmat` (boolean): Example: `true`
    - `unNumber` (string): The UN number for the hazardous material, if applicable. Example: `"UN1950"`

#### `Error`
- **Type:** `object`
- **Properties:**
    - `code` (integer, int32)
    - `message` (string)

### State Management
The Product Catalog Service uses **MongoDB** as its primary data store.
- **Aggregate Root:** The `Product` entity (`com.paklog.productcatalog.domain.model.Product`) serves as the aggregate root, ensuring transactional consistency for all operations related to a product.
- **Repository:** The `ProductRepository` (`com.paklog.productcatalog.domain.repository.ProductRepository`) defines the contract for persistence operations, which is implemented by an adapter in the `infrastructure.persistence` layer (e.g., `com.paklog.productcatalog.infrastructure.persistence.repository.MongoProductRepository`).
- **Data Model:** Products are stored as documents in a MongoDB collection, reflecting the `Product` schema.

### Events Emitted/Listened For
The service leverages **Apache Kafka** for asynchronous communication and event propagation.
- **Events Emitted:**
    - `ProductCreatedEvent`: Published when a new product is successfully added to the catalog.
    - `ProductUpdatedEvent`: Published when an existing product's details are modified.
    - `ProductDeletedEvent`: Published when a product is removed from the catalog.
- **Event Structure:** Events are defined in the `com.paklog.productcatalog.domain.event` package. They typically contain the `sku` of the product and relevant data changes.
- **Event Publishing:** The `DomainEventProcessor` (`com.paklog.productcatalog.application.service.DomainEventProcessor`) is responsible for publishing these events to Kafka topics.
- **Configuration:** Kafka broker details are configured in `application.yml` or `application-docker.yml`.

## 3. IMPLEMENTATION EXAMPLES

### Basic Usage Example: Create a Product
To create a new product, send a `POST` request to the `/products` endpoint with a `Product` object in the request body.

```bash
curl -X POST \
  http://localhost:8082/products \
  -H 'Content-Type: application/json' \
  -d '{
    "sku": "NEW-WIDGET-001",
    "title": "Brand New Super Widget",
    "dimensions": {
      "item": {
        "length": { "value": 15.0, "unit": "INCHES" },
        "width": { "value": 10.0, "unit": "INCHES" },
        "height": { "value": 5.0, "unit": "INCHES" },
        "weight": { "value": 2.5, "unit": "POUNDS" }
      },
      "package": {
        "length": { "value": 16.0, "unit": "INCHES" },
        "width": { "value": 11.0, "unit": "INCHES" },
        "height": { "value": 6.0, "unit": "INCHES" },
        "weight": { "value": 3.0, "unit": "POUNDS" }
      }
    },
    "attributes": {
      "hazmatInfo": {
        "isHazmat": false
      }
    }
  }'
```

### Advanced Configuration Example: Docker Deployment
The service can be deployed using Docker and configured via `application-docker.yml` and environment variables.

**`docker-compose.yml` snippet:**
```yaml
version: '3.8'
services:
  product-catalog:
    image: paklog/product-catalog:1.0.0-SNAPSHOT
    ports:
      - "8082:8082"
    environment:
      - SPRING_DATA_MONGODB_HOST=mongodb
      - SPRING_DATA_MONGODB_PORT=27017
      - SPRING_DATA_MONGODB_DATABASE=productcatalog
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
      - SERVER_PORT=8082
    depends_on:
      - mongodb
      - kafka
  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
volumes:
  mongodb_data:
```

### Customization Scenarios
- **Adding New Product Attributes:**
    1.  Modify the `Attributes` Value Object (`com.paklog.productcatalog.domain.model.Attributes`) to include new fields.
    2.  Update the `Product` schema in `openapi.yaml` to reflect the changes.
    3.  Adjust the DTOs (`infrastructure.web.dto`) and mappers (`infrastructure.web.mapper`) to handle the new attributes.
    4.  Update persistence logic (`infrastructure.persistence`) if the new attributes require specific handling.
- **Implementing New Business Rules:**
    1.  Add new validation logic within the `Product` aggregate or related Value Objects in the `domain.model` package.
    2.  Implement new application services (`application.service`) to orchestrate complex business workflows.
    3.  Ensure new rules are covered by unit and integration tests.

### Common Patterns and Best Practices
- **Domain-Driven Design (DDD):** Focus on the `Product` aggregate root, Value Objects, and clear separation of domain logic.
- **Hexagonal Architecture:** Maintain clear boundaries between domain, application, and infrastructure layers. Dependencies should flow inwards.
- **RESTful API Design:** Use standard HTTP methods, status codes, and resource-based URLs.
- **Event-Driven Architecture:** Leverage Kafka for asynchronous communication, promoting loose coupling between services.
- **Validation:** Utilize Spring Boot's validation annotations (`@Valid`, `@NotNull`, etc.) for input validation.
- **Immutability:** Favor immutable Value Objects to enhance data integrity and simplify reasoning.

## 4. TROUBLESHOOTING

### Common Errors and Their Solutions
- **`400 Bad Request` (Invalid Input):**
    - **Cause:** Request body does not conform to the expected schema or contains invalid data (e.g., missing required fields, invalid enum values, negative dimensions).
    - **Solution:** Review the API documentation (OpenAPI UI) for the correct request body structure and validation rules. Check server logs for specific validation error messages.
- **`404 Not Found` (Product Not Found):**
    - **Cause:** The requested product SKU does not exist in the catalog.
    - **Solution:** Verify the SKU in the request path. Ensure the product was successfully created.
- **`409 Conflict` (Product Already Exists):**
    - **Cause:** Attempting to create a product with an SKU that already exists.
    - **Solution:** Use a unique SKU for new products. If updating, use `PUT` or `PATCH` endpoints.
- **`500 Internal Server Error`:**
    - **Cause:** Generic server-side error. Could be issues with database connectivity, Kafka broker, unhandled exceptions in business logic, or misconfiguration.
    - **Solution:** Check the service logs for stack traces and error messages. Verify connectivity to MongoDB and Kafka. Use Actuator endpoints (`/actuator/health`) to check service health.

### Debugging Strategies
- **Local Debugging:** Run the Spring Boot application in an IDE (e.g., IntelliJ IDEA, Eclipse) with a debugger attached. Set breakpoints in controllers, services, and repository implementations.
- **Logging:** Configure logging levels (e.g., `DEBUG`, `TRACE`) in `application.yml` to get more detailed output.
- **Spring Boot Actuator:**
    - Access `/actuator/health` to check the health of various components (database, Kafka).
    - Access `/actuator/metrics` to monitor application metrics.
    - Access `/actuator/loggers` to dynamically change logging levels.
- **Testcontainers:** For integration tests, inspect the logs of the Testcontainers-managed MongoDB and Kafka instances for issues.

### Performance Considerations
- **k6 Load Tests:** Utilize the k6 load tests provided in the `k6/` directory to simulate various traffic patterns (load, stress, spike) and identify performance bottlenecks.
- **Monitoring:** Use Spring Boot Actuator and external monitoring tools to track response times, throughput, error rates, and resource utilization (CPU, memory, network I/O).
- **Database Indexing:** Ensure appropriate indexes are created in MongoDB for frequently queried fields (e.g., `sku`).
- **Kafka Producer/Consumer Tuning:** Optimize Kafka producer and consumer configurations for throughput and latency.

## 5. RELATED COMPONENTS

### Dependencies
The project relies on the following key dependencies (from `pom.xml`):
- **`org.springframework.boot:spring-boot-starter-web`**: For building RESTful APIs.
- **`org.springframework.boot:spring-boot-starter-data-mongodb`**: For MongoDB integration.
- **`org.springframework.kafka:spring-kafka`**: For Apache Kafka integration.
- **`org.springframework.boot:spring-boot-starter-validation`**: For declarative validation.
- **`org.springframework.boot:spring-boot-starter-actuator`**: For monitoring and management endpoints.
- **`org.springdoc:springdoc-openapi-starter-webmvc-ui`**: For OpenAPI documentation generation.
- **`com.tngtech.archunit:archunit-junit5`**: For architecture testing.
- **`org.testcontainers:*`**: For integration testing with Dockerized services (MongoDB, Kafka).

### Components Commonly Used Alongside This One
In a typical microservices ecosystem, the Product Catalog Service would interact with or be complemented by:
- **API Gateway:** To route external requests to the appropriate microservices, handle authentication/authorization, and provide a single entry point.
- **Service Registry/Discovery:** (e.g., Eureka, Consul) For services to register themselves and discover other services.
- **Message Broker (Kafka):** For asynchronous communication, event streaming, and decoupling services.
- **Database (MongoDB):** The primary data store for product information.
- **Monitoring and Alerting Systems:** (e.g., Prometheus, Grafana, ELK Stack) To collect metrics, logs, and provide alerts.
- **Authentication/Authorization Service:** To manage user identities and permissions.
- **Inventory Service:** To manage stock levels, often consuming `ProductCreatedEvent` or `ProductUpdatedEvent`.
- **Order Service:** To process customer orders, referencing product information from the Product Catalog.

### Alternative Approaches
- **Relational Database:** Instead of MongoDB, a relational database (e.g., PostgreSQL, MySQL) could be used with Spring Data JPA. This would be suitable if strong ACID transactions across multiple entities are a primary concern, though it might require more rigid schema management.
- **Different Messaging Queue:** Alternatives to Kafka include RabbitMQ, ActiveMQ, or cloud-native messaging services (e.g., AWS SQS/SNS, Azure Service Bus).
- **GraphQL API:** Instead of a REST API, a GraphQL endpoint could be exposed for more flexible data fetching by clients.
- **CQRS (Command Query Responsibility Segregation):** For highly complex domains, separating read and write models could be considered to optimize performance and scalability.
