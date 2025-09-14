// MongoDB initialization script
db = db.getSiblingDB('productcatalog');

// Create application user
db.createUser({
  user: 'productcatalog',
  pwd: 'productcatalog123',
  roles: [
    {
      role: 'readWrite',
      db: 'productcatalog'
    }
  ]
});

// Create collections with validation
db.createCollection('products', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['sku', 'title', 'createdAt', 'updatedAt'],
      properties: {
        sku: {
          bsonType: 'string',
          description: 'SKU must be a string and is required'
        },
        title: {
          bsonType: 'string',
          description: 'Title must be a string and is required'
        },
        dimensions: {
          bsonType: 'object',
          properties: {
            item: {
              bsonType: 'object',
              required: ['length', 'width', 'height', 'weight']
            },
            packageDimensions: {
              bsonType: 'object',
              required: ['length', 'width', 'height', 'weight']
            }
          }
        },
        attributes: {
          bsonType: 'object',
          properties: {
            hazmatInfo: {
              bsonType: 'object',
              required: ['isHazmat']
            }
          }
        },
        createdAt: {
          bsonType: 'date',
          description: 'CreatedAt must be a date and is required'
        },
        updatedAt: {
          bsonType: 'date',
          description: 'UpdatedAt must be a date and is required'
        }
      }
    }
  }
});

// Create indexes
db.products.createIndex({ sku: 1 }, { unique: true });
db.products.createIndex({ title: 1 });
db.products.createIndex({ createdAt: -1 });
db.products.createIndex({ updatedAt: -1 });

print('MongoDB initialized successfully for Product Catalog service');