package com.paklog.productcatalog.shared.validation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Aspect for automatically validating JSON objects against schemas
 * when methods are annotated with @ValidateJsonSchema.
 * This ensures data contract compliance across the Product Catalog service.
 */
@Aspect
@Component
public class JsonSchemaValidationAspect {

    private static final Logger logger = LoggerFactory.getLogger(JsonSchemaValidationAspect.class);

    private final JsonSchemaValidator jsonSchemaValidator;

    public JsonSchemaValidationAspect(JsonSchemaValidator jsonSchemaValidator) {
        this.jsonSchemaValidator = jsonSchemaValidator;
    }

    /**
     * Validates method parameters before method execution.
     */
    @Before("@annotation(validateJsonSchema)")
    public void validateParameters(JoinPoint joinPoint, ValidateJsonSchema validateJsonSchema) {
        if (validateJsonSchema.type() != ValidateJsonSchema.ValidationType.PARAMETER) {
            return;
        }

        Object[] args = joinPoint.getArgs();
        int paramIndex = validateJsonSchema.parameterIndex();

        if (paramIndex >= 0 && paramIndex < args.length) {
            Object parameter = args[paramIndex];
            if (parameter != null) {
                validateObject(parameter, validateJsonSchema.value(), getMethodName(joinPoint));
            }
        }
    }

    /**
     * Validates method return values after method execution.
     */
    @AfterReturning(pointcut = "@annotation(validateJsonSchema)", returning = "result")
    public void validateReturnValue(JoinPoint joinPoint, ValidateJsonSchema validateJsonSchema, Object result) {
        if (validateJsonSchema.type() != ValidateJsonSchema.ValidationType.RETURN_VALUE) {
            return;
        }

        if (result != null) {
            validateObject(result, validateJsonSchema.value(), getMethodName(joinPoint));
        }
    }

    /**
     * Validates parameter annotations on method parameters.
     */
    @Before("execution(* *(..)) && args(..) && @annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void validateAnnotatedParameters(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof ValidateJsonSchema validateJsonSchema) {
                    if (i < args.length && args[i] != null) {
                        validateObject(args[i], validateJsonSchema.value(), getMethodName(joinPoint));
                    }
                }
            }
        }
    }

    private void validateObject(Object obj, ValidateJsonSchema.SchemaType schemaType, String methodName) {
        try {
            JsonSchemaValidator.ValidationResult result = jsonSchemaValidator.validateAgainstSchema(
                    obj,
                    schemaType.getSchemaPath()
            );

            if (!result.isValid()) {
                String errorMessage = String.format(
                        "JSON Schema validation failed in method %s for schema %s: %s",
                        methodName,
                        schemaType.name(),
                        result.getErrorsAsString()
                );

                logger.error(errorMessage);
                throw new JsonSchemaValidator.ValidationException(errorMessage, null);
            }

            logger.debug("JSON Schema validation passed for {} in method {}", schemaType.name(), methodName);

        } catch (JsonSchemaValidator.ValidationException e) {
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format(
                    "Unexpected error during JSON Schema validation in method %s: %s",
                    methodName,
                    e.getMessage()
            );
            logger.error(errorMessage, e);
            throw new JsonSchemaValidator.ValidationException(errorMessage, e);
        }
    }

    private String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().toShortString();
    }
}