
import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

// Test configuration
export const options = {
  stages: [
    { duration: '2m', target: 100 }, // below normal load
    { duration: '5m', target: 100 },
    { duration: '2m', target: 200 }, // normal load
    { duration: '5m', target: 200 },
    { duration: '2m', target: 300 }, // around the breaking point
    { duration: '5m', target: 300 },
    { duration: '2m', target: 400 }, // beyond the breaking point
    { duration: '5m', target: 400 },
    { duration: '10m', target: 0 }, // scale down. Recovery stage.
  ],
  thresholds: {
    'http_req_duration': ['p(99)<1500'], // 99% of requests must complete below 1.5s
  },
};

// Test data
const data = new SharedArray('some data', function () {
    return [
        {
            sku: `TEST-SKU-${__VU}`,
            title: "Test Product",
            dimensions: {
                item: {
                    length: { value: 10, unit: "INCHES" },
                    width: { value: 10, unit: "INCHES" },
                    height: { value: 10, unit: "INCHES" },
                    weight: { value: 10, unit: "POUNDS" },
                },
                package: {
                    length: { value: 10, unit: "INCHES" },
                    width: { value: 10, unit: "INCHES" },
                    height: { value: 10, unit: "INCHES" },
                    weight: { value: 10, unit: "POUNDS" },
                },
            },
            attributes: {
                hazmatInfo: {
                    isHazmat: false,
                },
            },
        },
    ];
});

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8082';

export default function () {
  const product = data[0];
  product.sku = `TEST-SKU-${__VU}`;

  // Create a new product
  let createResponse = http.post(`${BASE_URL}/products`, JSON.stringify(product), {
    headers: { 'Content-Type': 'application/json' },
  });
  check(createResponse, { 'product created': (r) => r.status === 201 });

  sleep(1);

  // Get the product by SKU
  let getResponse = http.get(`${BASE_URL}/products/${product.sku}`);
  check(getResponse, { 'product retrieved': (r) => r.status === 200 });

  sleep(1);

  // List all products
  let listResponse = http.get(`${BASE_URL}/products`);
  check(listResponse, { 'products listed': (r) => r.status === 200 });

  sleep(1);

  // Update the product
  product.title = 'Updated Test Product';
  let updateResponse = http.put(`${BASE_URL}/products/${product.sku}`, JSON.stringify(product), {
    headers: { 'Content-Type': 'application/json' },
  });
  check(updateResponse, { 'product updated': (r) => r.status === 200 });

  sleep(1);

  // Patch the product
  let patchResponse = http.patch(`${BASE_URL}/products/${product.sku}`, JSON.stringify({ title: 'Patched Test Product' }), {
    headers: { 'Content-Type': 'application/json' },
  });
  check(patchResponse, { 'product patched': (r) => r.status === 200 });

  sleep(1);

  // Delete the product
  let deleteResponse = http.del(`${BASE_URL}/products/${product.sku}`);
  check(deleteResponse, { 'product deleted': (r) => r.status === 204 });
}
