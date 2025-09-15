package com.paklog.productcatalog.infrastructure.messaging.producer;

import com.paklog.productcatalog.application.port.output.DomainEventPublisher;
import com.paklog.productcatalog.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaDomainEventPublisher implements DomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaDomainEventPublisher.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicName;
    
    public KafkaDomainEventPublisher(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${product-catalog.kafka.topics.product-events}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }
    
    @Override
    public void publish(DomainEvent event) {
        logger.debug("Publishing domain event: {} with ID: {} to topic: {}", 
                    event.getEventType(), event.getEventId(), topicName);
        
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(topicName, event.getEventId(), event);
            
            // Use non-blocking callback instead of blocking wait
            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.error("Failed to publish event: {} with ID: {} to topic: {}", 
                               event.getEventType(), event.getEventId(), topicName, throwable);
                } else {
                    logger.debug("Successfully published event: {} with ID: {} to topic: {} at offset: {}", 
                               event.getEventType(), event.getEventId(), topicName, 
                               result.getRecordMetadata().offset());
                }
            });
            
        } catch (Exception e) {
            logger.error("Unexpected error while publishing event: {} with ID: {}", 
                        event.getEventType(), event.getEventId(), e);
            // Use specific exception instead of generic RuntimeException
            throw new RuntimeException("Failed to publish domain event: " + event.getEventType(), e);
        }
    }
}