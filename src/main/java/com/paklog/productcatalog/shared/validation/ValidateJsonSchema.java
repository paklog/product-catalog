package com.paklog.productcatalog.shared.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that should have their parameters or return values
 * validated against JSON schemas as part of the data mesh contract enforcement.
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateJsonSchema {

    /**
     * The schema type to validate against.
     */
    SchemaType value() default SchemaType.PRODUCT;

    /**
     * Whether to validate method parameters (default) or return value.
     */
    ValidationType type() default ValidationType.PARAMETER;

    /**
     * Parameter index to validate (for parameter validation).
     */
    int parameterIndex() default 0;

    enum SchemaType {
        PRODUCT("schemas/product-schema.json"),
        PRODUCT_EVENT("schemas/product-event-schema.json");

        private final String schemaPath;

        SchemaType(String schemaPath) {
            this.schemaPath = schemaPath;
        }

        public String getSchemaPath() {
            return schemaPath;
        }
    }

    enum ValidationType {
        PARAMETER,
        RETURN_VALUE
    }
}