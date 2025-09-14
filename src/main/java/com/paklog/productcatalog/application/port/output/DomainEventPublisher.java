package com.paklog.productcatalog.application.port.output;

import com.paklog.productcatalog.domain.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}