package com.paklog.productcatalog.shared.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for validating JSON objects against JSON Schema specifications.
 * This service implements data contract validation for the Product Catalog service
 * as part of the data mesh governance strategy.
 */
@Component
public class JsonSchemaValidator {

    private static final Logger logger = LoggerFactory.getLogger(JsonSchemaValidator.class);

    private final ObjectMapper objectMapper;
    private final JsonSchemaFactory schemaFactory;

    public JsonSchemaValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.schemaFactory = JsonSchemaFactory.byDefault();
    }

    /**
     * Validates a JSON object against the Product schema.
     *
     * @param jsonData the JSON data to validate
     * @return validation result containing any errors
     * @throws ValidationException if validation fails due to schema loading or processing errors
     */
    public ValidationResult validateProduct(Object jsonData) {
        return validateAgainstSchema(jsonData, "schemas/product-schema.json");
    }

    /**
     * Validates a JSON object against the Product Event schema.
     *
     * @param jsonData the JSON data to validate
     * @return validation result containing any errors
     * @throws ValidationException if validation fails due to schema loading or processing errors
     */
    public ValidationResult validateProductEvent(Object jsonData) {
        return validateAgainstSchema(jsonData, "schemas/product-event-schema.json");
    }

    /**
     * Generic method to validate JSON data against a specified schema.
     *
     * @param jsonData the JSON data to validate
     * @param schemaPath the classpath to the JSON schema file
     * @return validation result containing any errors
     * @throws ValidationException if validation fails due to schema loading or processing errors
     */
    public ValidationResult validateAgainstSchema(Object jsonData, String schemaPath) {
        try {
            // Convert object to JsonNode
            JsonNode dataNode = objectMapper.valueToTree(jsonData);

            // Load schema from classpath
            JsonNode schemaNode = loadSchemaFromClasspath(schemaPath);
            JsonSchema schema = schemaFactory.getJsonSchema(schemaNode);

            // Perform validation
            ProcessingReport report = schema.validate(dataNode);

            return createValidationResult(report);

        } catch (IOException | ProcessingException e) {
            logger.error("Error validating JSON against schema {}: {}", schemaPath, e.getMessage(), e);
            throw new ValidationException("Schema validation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Validates a JSON string against a specified schema.
     *
     * @param jsonString the JSON string to validate
     * @param schemaPath the classpath to the JSON schema file
     * @return validation result containing any errors
     * @throws ValidationException if validation fails due to schema loading or processing errors
     */
    public ValidationResult validateJsonString(String jsonString, String schemaPath) {
        try {
            JsonNode dataNode = objectMapper.readTree(jsonString);
            JsonNode schemaNode = loadSchemaFromClasspath(schemaPath);
            JsonSchema schema = schemaFactory.getJsonSchema(schemaNode);

            ProcessingReport report = schema.validate(dataNode);
            return createValidationResult(report);

        } catch (IOException | ProcessingException e) {
            logger.error("Error validating JSON string against schema {}: {}", schemaPath, e.getMessage(), e);
            throw new ValidationException("Schema validation failed: " + e.getMessage(), e);
        }
    }

    private JsonNode loadSchemaFromClasspath(String schemaPath) throws IOException {
        ClassPathResource resource = new ClassPathResource(schemaPath);
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readTree(inputStream);
        }
    }

    private ValidationResult createValidationResult(ProcessingReport report) {
        List<String> errors = new ArrayList<>();

        if (!report.isSuccess()) {
            report.forEach(message -> {
                errors.add(message.getMessage());
            });
        }

        return new ValidationResult(report.isSuccess(), errors);
    }

    /**
     * Result of JSON schema validation.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors != null ? List.copyOf(errors) : List.of();
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorsAsString() {
            return String.join(", ", errors);
        }

        @Override
        public String toString() {
            return "ValidationResult{" +
                    "valid=" + valid +
                    ", errors=" + errors +
                    '}';
        }
    }

    /**
     * Exception thrown when JSON schema validation encounters processing errors.
     */
    public static class ValidationException extends RuntimeException {
        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}