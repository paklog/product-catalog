package com.paklog.productcatalog.shared.exception;

public class EventPublishingException extends RuntimeException {
    
    private final String eventType;
    private final String eventId;
    
    public EventPublishingException(String eventType, String eventId, String message, Throwable cause) {
        super(String.format("Failed to publish event %s with ID %s: %s", eventType, eventId, message), cause);
        this.eventType = eventType;
        this.eventId = eventId;
    }
    
    public EventPublishingException(String eventType, String eventId, Throwable cause) {
        this(eventType, eventId, cause.getMessage(), cause);
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public String getEventId() {
        return eventId;
    }
}