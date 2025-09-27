
# Pre-load Products

This directory contains a Postman collection and a data file to pre-load the Product Catalog API with 50 products.

## Prerequisites

- [Node.js and npm](https://nodejs.org/en/download/) installed on your local machine.
- [Newman](https://learning.postman.com/docs/collections/using-newman-cli/installing-running-newman/) installed globally:

```bash
npm install -g newman
```

## Running the Collection

To run the collection, navigate to this directory in your terminal and use the following command:

```bash
newman run product-catalog.postman_collection.json -e .. -d products.json
```

### Environment Variables

The `BASE_URL` environment variable should be set to the base URL of the API. By default, it is set to `http://localhost:8082` in the collection.

You can override this by creating a Postman environment file (e.g., `dev.postman_environment.json`) and passing it to Newman with the `-e` flag. A ready-to-use `dev.postman_environment.json` is already included in this directory:

```json
{
	"id": "a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6",
	"name": "dev",
	"values": [
		{
			"key": "BASE_URL",
			"value": "http://localhost:8082",
			"enabled": true
		}
	],
	"_postman_variable_scope": "environment"
}
```

And then run Newman with the environment file:

```bash
newman run product-catalog.postman_collection.json -e dev.postman_environment.json -d products.json
```

## Data File

The `products.json` file contains an array of 50 product objects. Newman will iterate through this array and send a request to the `POST /products` endpoint for each product.

The Postman collection is configured to use variables from this data file (e.g., `{{sku}}`, `{{title}}`). These variables are mapped to the corresponding fields in the `products.json` file.
