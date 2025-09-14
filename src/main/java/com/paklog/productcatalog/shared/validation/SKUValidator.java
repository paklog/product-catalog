package com.paklog.productcatalog.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SKUValidator implements ConstraintValidator<ValidSKU, String> {
    
    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9-_]{2,49}$");
    
    @Override
    public void initialize(ValidSKU constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String sku, ConstraintValidatorContext context) {
        if (sku == null || sku.trim().isEmpty()) {
            return false;
        }
        
        return SKU_PATTERN.matcher(sku.trim().toUpperCase()).matches();
    }
}