
# k6 Load Testing

This directory contains k6 scripts for load testing the Product Catalog API.

## Prerequisites

- [k6](httpss://k6.io/docs/getting-started/installation/) installed on your local machine.

## Test Scenarios

The following test scenarios are available:

- **`load-test.js`**: A standard load test that simulates a moderate amount of traffic to the API. This is useful for identifying performance bottlenecks and ensuring the application can handle expected traffic levels.
- **`stress-test.js`**: A stress test that gradually increases the load on the API to determine its breaking point. This is useful for understanding the application's limits and how it behaves under extreme conditions.
- **`spike-test.js`**: A spike test that simulates sudden bursts of traffic to the API. This is useful for testing the application's ability to handle sudden changes in traffic and recover from them.

## Running the Tests

To run the tests, navigate to this directory in your terminal and use the following commands:

### Using k6 directly

#### Load Test

```bash
k6 run load-test.js
```

#### Stress Test

```bash
k6 run stress-test.js
```

#### Spike Test

```bash
k6 run spike-test.js
```

### Using npm

You can also use the npm scripts defined in `package.json` to run the tests:

```bash
npm run load-test
npm run stress-test
npm run spike-test
```

## Test Configuration

The test scripts are configured using the `options` object at the top of each file. You can modify these options to change the test duration, number of virtual users (VUs), and other parameters.

### Environment Variables

The base URL of the API can be configured using the `BASE_URL` environment variable. If this variable is not set, the tests will default to `http://localhost:8080`.

Example:

```bash
BASE_URL=https://api.example.com/v1 k6 run load-test.js
```
