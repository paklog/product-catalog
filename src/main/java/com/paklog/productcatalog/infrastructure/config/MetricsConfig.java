package com.paklog.productcatalog.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    
    @Bean
    public Counter productCreatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("product.created")
                .description("Number of products created")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter productUpdatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("product.updated")
                .description("Number of products updated")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter productDeletedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("product.deleted")
                .description("Number of products deleted")
                .register(meterRegistry);
    }
    
    @Bean
    public Timer productOperationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("product.operation")
                .description("Time taken for product operations")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter domainEventPublishedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("domain.event.published")
                .description("Number of domain events published")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter domainEventFailedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("domain.event.failed")
                .description("Number of failed domain event publications")
                .register(meterRegistry);
    }
}