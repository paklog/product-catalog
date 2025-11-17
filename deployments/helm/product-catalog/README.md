# Product Catalog Helm Chart

A production-ready Helm chart for deploying the Product Catalog Service with support for both JVM and GraalVM Native Image deployments.

## Features

- **Dual deployment modes**: JVM (default) or GraalVM Native Image
- **MongoDB integration**: Full MongoDB configuration with connection URI secrets
- **Kafka messaging**: Producer and consumer configuration
- **Kubernetes Gateway API**: Modern ingress with HTTPRoute
- **Production-ready**: Security hardening, health checks, resource limits
- **Observability**: Prometheus metrics, health endpoints, distributed tracing
- **Autoscaling**: Horizontal Pod Autoscaler support
- **High availability**: Pod anti-affinity, multiple replicas

## Quick Start

### Install with JVM (Default)

```bash
helm install product-catalog ./product-catalog \
  --set image.tag=1.0.0-SNAPSHOT \
  --set secrets.mongodb.uri="mongodb://user:pass@mongodb:27017/productcatalog"
```

### Install with Native Image

```bash
helm install product-catalog ./product-catalog \
  -f values-native.yaml \
  --set image.tag=1.0.0-SNAPSHOT \
  --set secrets.mongodb.uri="mongodb://user:pass@mongodb:27017/productcatalog"
```

## Configuration

### Image Configuration

| Parameter | Description | Default | Native |
|-----------|-------------|---------|--------|
| `image.repository` | Image repository | `paklog/product-catalog` | - |
| `image.tag` | Image tag | `""` (uses appVersion) | - |
| `image.pullPolicy` | Pull policy | `IfNotPresent` | - |
| `image.native` | Enable native image optimizations | `false` | `true` |

### Resource Limits

**JVM Mode** (default):
```yaml
resources:
  jvm:
    limits:
      cpu: 500m
      memory: 512Mi
    requests:
      cpu: 250m
      memory: 256Mi
```

**Native Image Mode** (when `image.native=true`):
```yaml
resources:
  native:
    limits:
      cpu: 200m
      memory: 256Mi
    requests:
      cpu: 100m
      memory: 128Mi
```

### Health Probes

**JVM Mode**:
- Liveness: 30s initial delay
- Readiness: 20s initial delay

**Native Image Mode**:
- Liveness: 10s initial delay
- Readiness: 5s initial delay

### MongoDB Configuration

```yaml
config:
  spring:
    data:
      mongodb:
        uri: mongodb://mongodb:27017/productcatalog
        autoIndexCreation: true

# Or use secrets (recommended for production)
secrets:
  mongodb:
    uri: "mongodb://user:password@mongodb:27017/productcatalog"
```

### Kafka Configuration

```yaml
config:
  spring:
    kafka:
      bootstrapServers: kafka:9092
      producer:
        keySerializer: org.apache.kafka.common.serialization.StringSerializer
        valueSerializer: org.springframework.kafka.support.serializer.JsonSerializer
      consumer:
        groupId: product-catalog-service
        keyDeserializer: org.apache.kafka.common.serialization.StringDeserializer
        valueDeserializer: org.springframework.kafka.support.serializer.JsonDeserializer
```

### Gateway API (Modern Ingress)

```yaml
gateway:
  enabled: true
  gatewayClassName: "istio"  # or "nginx", "contour", etc.
  parentRefs:
    - name: main-gateway
      namespace: default
      sectionName: http
  hostnames:
    - "product-catalog.example.com"
```

## Deployment Examples

### Development (with dependencies)

```bash
helm install product-catalog ./product-catalog \
  --set mongodb.enabled=true \
  --set kafka.enabled=true \
  --set gateway.enabled=false
```

### Production (JVM)

```bash
helm install product-catalog ./product-catalog \
  --set image.tag=1.0.0 \
  --set replicaCount=3 \
  --set autoscaling.enabled=true \
  --set gateway.enabled=true \
  --set gateway.hostnames[0]="api.production.com" \
  --set secrets.mongodb.uri="${MONGODB_URI}"
```

### Production (Native Image)

```bash
helm install product-catalog ./product-catalog \
  -f values-native.yaml \
  --set image.tag=1.0.0-SNAPSHOT \
  --set replicaCount=3 \
  --set gateway.hostnames[0]="api.production.com" \
  --set secrets.mongodb.uri="${MONGODB_URI}"
```

## Upgrade

```bash
helm upgrade product-catalog ./product-catalog \
  --set image.tag=1.1.0 \
  --reuse-values
```

## Uninstall

```bash
helm uninstall product-catalog
```

## Monitoring

### Health Endpoints

- **Liveness**: `http://pod-ip:8082/actuator/health/liveness`
- **Readiness**: `http://pod-ip:8082/actuator/health/readiness`
- **Metrics**: `http://pod-ip:8082/actuator/prometheus`

### Grafana Dashboards

The service exposes Prometheus metrics that can be visualized in Grafana:

- JVM metrics (if using JVM mode)
- Application metrics
- MongoDB connection pool metrics
- Kafka producer/consumer metrics

## Security

### Pod Security Context

```yaml
podSecurityContext:
  runAsNonRoot: true
  runAsUser: 1000
  fsGroup: 1000

securityContext:
  allowPrivilegeEscalation: false
  capabilities:
    drop:
      - ALL
  readOnlyRootFilesystem: true
```

### Secrets Management

**Option 1: Direct (Development)**
```bash
--set secrets.mongodb.uri="mongodb://..."
```

**Option 2: External Secrets Operator (Production)**
```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: product-catalog-mongodb
spec:
  secretStoreRef:
    name: aws-secretsmanager
  target:
    name: product-catalog
  data:
    - secretKey: mongodb-uri
      remoteRef:
        key: prod/product-catalog/mongodb
```

**Option 3: Sealed Secrets (Production)**
```bash
kubectl create secret generic product-catalog \
  --from-literal=mongodb-uri="mongodb://..." \
  --dry-run=client -o yaml | \
  kubeseal -o yaml | \
  kubectl apply -f -
```

## Native Image vs JVM Comparison

| Metric | JVM | Native Image |
|--------|-----|--------------|
| **Startup Time** | 3-5 seconds | 0.1-0.5 seconds |
| **Memory Usage** | 256-512 MB | 128-256 MB |
| **CPU Usage** | 250-500m | 100-200m |
| **Image Size** | ~300 MB | ~50-100 MB |
| **Build Time** | 1-2 minutes | 5-10 minutes |
| **First Request** | Slower (JIT) | Consistent |
| **Peak Performance** | Higher (JIT) | Slightly lower |

**Recommendation**: Use Native Image for:
- Cost optimization (lower resource usage)
- Fast scaling scenarios
- Serverless/FaaS deployments
- Microservices with frequent restarts

Use JVM for:
- CPU-intensive workloads
- Applications needing peak performance
- Easier debugging

## Troubleshooting

### Pod not starting

```bash
kubectl describe pod -l app.kubernetes.io/name=product-catalog
kubectl logs -l app.kubernetes.io/name=product-catalog --tail=100
```

### MongoDB connection issues

```bash
kubectl exec -it deployment/product-catalog -- env | grep MONGO
```

### Gateway not routing

```bash
kubectl get httproute -n <namespace>
kubectl describe httproute product-catalog
```

## Values Files

- `values.yaml`: Default JVM configuration
- `values-native.yaml`: Optimized for GraalVM Native Image

## Chart Information

- **Chart Version**: 0.1.0
- **App Version**: 1.1.0
- **Kubernetes**: >= 1.24
- **Gateway API**: v1 (optional)

## Support

For issues and questions:
- GitHub: https://github.com/paklog/product-catalog
- Chart bugs: Create an issue with label `helm`
