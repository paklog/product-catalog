package com.paklog.productcatalog.shared.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class MetricsUtil {
    
    private final Counter productCreatedCounter;
    private final Counter productUpdatedCounter;
    private final Counter productDeletedCounter;
    private final Timer productOperationTimer;
    private final Counter domainEventPublishedCounter;
    private final Counter domainEventFailedCounter;
    
    public MetricsUtil(Counter productCreatedCounter,
                      Counter productUpdatedCounter,
                      Counter productDeletedCounter,
                      Timer productOperationTimer,
                      Counter domainEventPublishedCounter,
                      Counter domainEventFailedCounter) {
        this.productCreatedCounter = productCreatedCounter;
        this.productUpdatedCounter = productUpdatedCounter;
        this.productDeletedCounter = productDeletedCounter;
        this.productOperationTimer = productOperationTimer;
        this.domainEventPublishedCounter = domainEventPublishedCounter;
        this.domainEventFailedCounter = domainEventFailedCounter;
    }
    
    public void incrementProductCreated() {
        productCreatedCounter.increment();
    }
    
    public void incrementProductUpdated() {
        productUpdatedCounter.increment();
    }
    
    public void incrementProductDeleted() {
        productDeletedCounter.increment();
    }
    
    public Timer.Sample startTimer() {
        return Timer.start();
    }
    
    public void stopTimer(Timer.Sample sample) {
        sample.stop(productOperationTimer);
    }
    
    public void incrementDomainEventPublished() {
        domainEventPublishedCounter.increment();
    }
    
    public void incrementDomainEventFailed() {
        domainEventFailedCounter.increment();
    }
}