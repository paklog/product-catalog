package com.paklog.productcatalog.application.service;

import com.paklog.productcatalog.application.port.output.DomainEventPublisher;
import com.paklog.productcatalog.domain.event.DomainEvent;
import com.paklog.productcatalog.domain.model.Product;
import com.paklog.productcatalog.shared.exception.EventPublishingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class DomainEventProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(DomainEventProcessor.class);
    
    private final DomainEventPublisher eventPublisher;
    
    public DomainEventProcessor(DomainEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Processes and clears domain events from the product synchronously.
     * This method extracts events and delegates to async processing.
     */
    public void processAndClear(Product product) {
        List<DomainEvent> events = List.copyOf(product.getDomainEvents());
        product.clearDomainEvents();
        
        if (!events.isEmpty()) {
            publishEventsAsync(events);
        }
    }
    
    /**
     * Publishes events asynchronously in batch.
     * Errors are logged but don't block the main transaction.
     */
    @Async("eventExecutor")
    public CompletableFuture<Void> publishEventsAsync(List<DomainEvent> events) {
        logger.debug("Publishing {} domain events asynchronously", events.size());
        
        try {
            for (DomainEvent event : events) {
                publishSingleEvent(event);
            }
            logger.debug("Successfully published {} events", events.size());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            logger.error("Failed to publish domain events batch of size {}", events.size(), e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private void publishSingleEvent(DomainEvent event) {
        try {
            eventPublisher.publish(event);
            logger.debug("Published domain event: {} with ID: {}", event.getEventType(), event.getEventId());
        } catch (Exception e) {
            logger.error("Failed to publish domain event: {} with ID: {}", 
                        event.getEventType(), event.getEventId(), e);
            throw new EventPublishingException(event.getEventType(), event.getEventId(), e);
        

}
}
}
