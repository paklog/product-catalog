# Product Catalog API: Developer Guide

## 1. Introduction

Welcome to the Product Catalog API! This guide provides everything you need to start using our RESTful API to manage your product data.

The API is built on Domain-Driven Design (DDD) principles, with the **Product** serving as the central "Aggregate" entity. This means that all related product information, such as its dimensions, weight, and compliance attributes, is managed directly through the `Product` resource, ensuring data integrity and consistency.

Our API supports full CRUD (Create, Read, Update, Delete) functionality, uses standard HTTP verbs and status codes, and provides clear, predictable data structures.

### Key Features:
- **RESTful Architecture:** A predictable, resource-oriented design.
- **CRUD Operations:** Full control over your product data.
- **Detailed Data Models:** Manage not just the product, but its item/package dimensions and compliance data.
- **Standard Error Handling:** Clear and consistent error messages.
- **Pagination:** Efficiently browse large product catalogs.

## 2. Getting Started

### Base URLs
The API is available under the following base URLs:

- **Development:** `http://localhost:8082`
- **Production:** `https://api.example.com/v1`

All endpoint paths in this document should be appended to these base URLs.

### Authentication
*(Note: The current OpenAPI specification does not define an authentication scheme. In a production environment, you would typically need to include an Authorization header with a Bearer Token or API Key.)*

```bash
# Example of a request with an authentication header
curl --request GET \
  --url 'https://api.example.com/v1/products' \
  --header 'Authorization: Bearer YOUR_ACCESS_TOKEN'
```

## 3. Core Concepts

The API revolves around the **`Product`** resource.

- **Product:** The central entity in the catalog, identified by a unique `sku`. It acts as an "aggregate" that bundles all its related data.
- **Dimensions:** Contains two sets of physical measurements:
    - `item`: The dimensions and weight of the product itself.
    - `package`: The dimensions and weight of the product in its shipping packaging.
- **Attributes:** A flexible container for additional data, such as compliance information.
    - `hazmat_info`: Specifies if a product is a hazardous material and its UN number.
- **SKU (Stock Keeping Unit):** The unique identifier for a product (e.g., `WIDGET-123`). It is provided by the client and used in the API path to reference a specific product.

## 4. API Endpoints

This section details each available endpoint, its purpose, and how to use it.

---

### 4.1. Create a New Product

Adds a new product to the catalog. The `sku` must be unique.

`POST /products`

#### Request
The request body must contain a full `Product` object.

**Example `cURL` Request:**
```bash
curl --request POST \
  --url 'http://localhost:8082/products' \
  --header 'Content-Type: application/json' \
  --data '{ "sku": "WIDGET-001", "title": "Industrial Grade Widget", "dimensions": { "item": { "length": {"value": 10.5, "unit": "INCHES"}, "width": {"value": 8.0, "unit": "INCHES"}, "height": {"value": 3.2, "unit": "INCHES"}, "weight": {"value": 5.0, "unit": "POUNDS"} }, "package": { "length": {"value": 12.0, "unit": "INCHES"}, "width": {"value": 9.5, "unit": "INCHES"}, "height": {"value": 4.0, "unit": "INCHES"}, "weight": {"value": 5.8, "unit": "POUNDS"} } }, "attributes": { "hazmat_info": { "is_hazmat": true, "un_number": "UN1950" } } }'
```

#### Responses
- **`201 Created`**: The product was created successfully. The response body will contain the newly created product object.
- **`400 Bad Request`**: The request body is invalid (e.g., missing required fields, invalid data types).
- **`409 Conflict`**: A product with the given `sku` already exists.

---

### 4.2. List All Products

Retrieves a paginated list of all products.

`GET /products`

#### Query Parameters
- `limit` (integer, optional, default: 20): The number of products to return.
- `offset` (integer, optional, default: 0): The number of products to skip from the beginning of the list.

#### Request
**Example `cURL` Request (get the first 10 products):**
```bash
curl --request GET \
  --url 'http://localhost:8082/products?limit=10&offset=0'
```

#### Responses
- **`200 OK`**: Successfully retrieved the list of products. The body contains a `ProductPage` object.

**Example `ProductPage` Response Body:**
```json
{ "content": [ { "sku": "WIDGET-001", "title": "Industrial Grade Widget", // ... other product fields }, { "sku": "GADGET-002", "title": "Compact Digital Gadget", // ... other product fields } ], "total_pages": 10, "total_elements": 98, "current_page": 0, "page_size": 10, "is_first": true, "is_last": false }
```

---

### 4.3. Get a Specific Product

Retrieves a single product by its unique `sku`.

`GET /products/{sku}`

#### Request
The `{sku}` in the path must be the unique identifier of the product you wish to retrieve.

**Example `cURL` Request:**
```bash
curl --request GET \
  --url 'http://localhost:8082/products/WIDGET-001'
```

#### Responses
- **`200 OK`**: The product was found and is returned in the response body.
- **`404 Not Found`**: No product with the specified `sku` exists.

---

### 4.4. Update a Product (Full Replace)

Replaces an entire existing product with new data. This is an idempotent operation.

`PUT /products/{sku}`

#### Request
The request body must contain a complete `Product` object. Any optional fields that are omitted may be cleared or reset to their default values.

**Example `cURL` Request (updating the title):**
```bash
curl --request PUT \
  --url 'http://localhost:8082/products/WIDGET-001' \
  --header 'Content-Type: application/json' \
  --data '{ "sku": "WIDGET-001", "title": "Updated Industrial Widget", "dimensions": { "item": { "length": {"value": 10.5, "unit": "INCHES"}, "width": {"value": 8.0, "unit": "INCHES"}, "height": {"value": 3.2, "unit": "INCHES"}, "weight": {"value": 5.0, "unit": "POUNDS"} }, "package": { "length": {"value": 12.0, "unit": "INCHES"}, "width": {"value": 9.5, "unit": "INCHES"}, "height": {"value": 4.0, "unit": "INCHES"}, "weight": {"value": 5.8, "unit": "POUNDS"} } }, "attributes": { "hazmat_info": { "is_hazmat": true, "un_number": "UN1950" } } }'
```

#### Responses
- **`200 OK`**: The product was updated successfully. The response body contains the updated product object.
- **`400 Bad Request`**: The request body is invalid.
- **`404 Not Found`**: No product with the specified `sku` exists.

---

### 4.5. Partially Update a Product

Updates one or more fields of an existing product. Fields not included in the request body will not be changed.

`PATCH /products/{sku}`

#### Request
The request body should contain only the fields you wish to change.

**Example `cURL` Request (updating only the title and package weight):**
```bash
curl --request PATCH \
  --url 'http://localhost:8082/products/WIDGET-001' \
  --header 'Content-Type: application/json' \
  --data '{ "title": "New Widget Title", "dimensions": { "package": { "weight": { "value": 6.1, "unit": "POUNDS" } } } }'
```

#### Responses
- **`200 OK`**: The product was updated successfully. The response body contains the complete, merged product object.
- **`400 Bad Request`**: The fields in the request body are invalid.
- **`404 Not Found`**: No product with the specified `sku` exists.

---

### 4.6. Delete a Product

Permanently deletes a product from the catalog.

`DELETE /products/{sku}`

#### Request
**Example `cURL` Request:**
```bash
curl --request DELETE \
  --url 'http://localhost:8082/products/WIDGET-001'
```

#### Responses
- **`204 No Content`**: The product was deleted successfully. No response body is returned.
- **`404 Not Found`**: No product with the specified `sku` exists.

## 5. Data Models (Schemas)

### `Product`
| Field | Type | Description | Required |
|---|---|---|---|
| `sku` | string | The unique, seller-defined Stock Keeping Unit. | Yes |
| `title` | string | The display name of the product. | Yes |
| `dimensions` | Dimensions | Physical dimensions of the item and its packaging. | No |
| `attributes` | Attributes | Additional product characteristics and compliance data. | No |

### `Dimensions`
| Field | Type | Description | Required |
|---|---|---|---|
| `item` | DimensionSet | The dimensions and weight of the product itself. | Yes |
| `package` | DimensionSet | The dimensions and weight of the product including its packaging. | Yes |

### `DimensionSet`
| Field | Type | Description | Required |
|---|---|---|---|
| `length` | DimensionMeasurement | The length of the object. | Yes |
| `width` | DimensionMeasurement | The width of the object. | Yes |
| `height` | DimensionMeasurement | The height of the object. | Yes |
| `weight` | WeightMeasurement | The weight of the object. | Yes |

### `DimensionMeasurement`
| Field | Type | Description | Required |
|---|---|---|---|
| `value` | number | Numeric value of the measurement (e.g., 10.5). Must be > 0. | Yes |
| `unit` | string | Unit of measurement. Enum: `INCHES`, `CENTIMETERS`, `MILLIMETERS`, `FEET`, `METERS`. | Yes |

### `WeightMeasurement`
| Field | Type | Description | Required |
|---|---|---|---|
| `value` | number | Numeric value of the weight (e.g., 5.8). Must be > 0. | Yes |
| `unit` | string | Unit of measurement. Enum: `POUNDS`, `KILOGRAMS`, `GRAMS`, `OUNCES`. | Yes |

### `Attributes`
| Field | Type | Description | Required |
|---|---|---|---|
| `hazmat_info` | HazmatInfo | Information related to hazardous material classification. | Yes |

### `HazmatInfo`
| Field | Type | Description | Required |
|---|---|---|---|
| `is_hazmat` | boolean | Indicates whether the product is hazardous material. | Yes |
| `un_number` | string | UN number for the hazardous material (required if `is_hazmat` is true). | No |

## 6. Error Handling

The API uses standard HTTP status codes to indicate the success or failure of a request. For client errors (4xx status codes), the response body will contain a standardized `Error` object.

### `Error` Schema
| Field | Type | Description |
|---|---|---|
| `code` | integer | Numeric error code aligned with the HTTP status. |
| `message` | string | A human-readable explanation of the error. |

**Example Error Response (`404 Not Found`):**
```json
{ "code": 404, "message": "Product with SKU 'UNKNOWN-SKU-999' not found" }
```
