package com.paklog.productcatalog.config;

import com.paklog.productcatalog.domain.event.ProductCreatedEvent;
import com.paklog.productcatalog.domain.event.ProductDeletedEvent;
import com.paklog.productcatalog.domain.event.ProductUpdatedEvent;
import com.paklog.productcatalog.domain.model.Product;
import com.paklog.productcatalog.infrastructure.persistence.entity.ProductEntity;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

/**
 * Registers runtime hints for GraalVM native image compilation.
 * This ensures that resources, reflection, and serialization work correctly
 * in the native executable.
 */
public class NativeRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Register JSON schema resources
        hints.resources()
                .registerPattern("schemas/*.json")
                .registerPattern("schemas/**/*.json");

        // Register application configuration resources
        hints.resources()
                .registerPattern("application*.yml")
                .registerPattern("application*.yaml")
                .registerPattern("application*.properties");

        // Register domain model classes for reflection (needed for Jackson serialization)
        registerReflection(hints, Product.class);
        registerReflection(hints, ProductEntity.class);

        // Register event classes for reflection (needed for Kafka serialization)
        registerReflection(hints, ProductCreatedEvent.class);
        registerReflection(hints, ProductUpdatedEvent.class);
        registerReflection(hints, ProductDeletedEvent.class);

        // Register value objects that might be used in serialization
        try {
            registerReflection(hints, Class.forName("com.paklog.productcatalog.domain.model.Dimensions"));
            registerReflection(hints, Class.forName("com.paklog.productcatalog.domain.model.Attributes"));
            registerReflection(hints, Class.forName("com.paklog.productcatalog.domain.model.SKU"));
            registerReflection(hints, Class.forName("com.paklog.productcatalog.domain.model.HazmatInfo"));
            registerReflection(hints, Class.forName("com.paklog.productcatalog.domain.model.DimensionMeasurement"));
            registerReflection(hints, Class.forName("com.paklog.productcatalog.domain.model.WeightMeasurement"));
            registerReflection(hints, Class.forName("com.paklog.productcatalog.domain.model.DimensionSet"));
        } catch (ClassNotFoundException e) {
            // Some classes might not exist, that's okay
        }
    }

    /**
     * Registers a class for reflection with common member categories needed for
     * serialization, deserialization, and framework operations.
     */
    private void registerReflection(RuntimeHints hints, Class<?> clazz) {
        hints.reflection().registerType(
                TypeReference.of(clazz),
                hint -> hint.withMembers(
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                        MemberCategory.INVOKE_PUBLIC_METHODS,
                        MemberCategory.DECLARED_FIELDS,
                        MemberCategory.PUBLIC_FIELDS
                )
        );
    }
}
