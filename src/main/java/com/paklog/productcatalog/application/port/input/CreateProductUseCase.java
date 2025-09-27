package com.paklog.productcatalog.application.port.input;

import com.paklog.productcatalog.application.command.CreateProductCommand;
import com.paklog.productcatalog.domain.model.Product;
import jakarta.validation.Valid;

public interface CreateProductUseCase {
    Product createProduct(@Valid CreateProductCommand command);
}