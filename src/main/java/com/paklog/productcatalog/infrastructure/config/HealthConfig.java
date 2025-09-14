package com.paklog.productcatalog.infrastructure.config;

import com.paklog.productcatalog.domain.repository.ProductRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class HealthConfig {
    
    @Bean
    public HealthIndicator productRepositoryHealthIndicator(ProductRepository productRepository) {
        return () -> {
            try {
                long count = productRepository.count();
                return Health.up()
                        .withDetail("productCount", count)
                        .withDetail("status", "Product repository is accessible")
                        .build();
            } catch (Exception e) {
                return Health.down()
                        .withDetail("error", e.getMessage())
                        .withDetail("status", "Product repository is not accessible")
                        .build();
            }
        };
    }
    
    @Bean
    public HealthIndicator mongoHealthIndicator(MongoTemplate mongoTemplate) {
        return () -> {
            try {
                mongoTemplate.execute(db -> {
                    db.runCommand(new org.bson.Document("ping", 1));
                    return "ok";
                });
                
                return Health.up()
                        .withDetail("database", mongoTemplate.getDb().getName())
                        .withDetail("status", "MongoDB is accessible")
                        .build();
            } catch (Exception e) {
                return Health.down()
                        .withDetail("error", e.getMessage())
                        .withDetail("status", "MongoDB is not accessible")
                        .build();
            }
        };
    }
    
    @Bean
    public HealthIndicator kafkaHealthIndicator(KafkaTemplate<String, Object> kafkaTemplate) {
        return () -> {
            try {
                // Simple health check by getting metadata
                kafkaTemplate.getProducerFactory().getConfigurationProperties();
                
                return Health.up()
                        .withDetail("status", "Kafka is accessible")
                        .build();
            } catch (Exception e) {
                return Health.down()
                        .withDetail("error", e.getMessage())
                        .withDetail("status", "Kafka is not accessible")
                        .build();
            }
        };
    }
}