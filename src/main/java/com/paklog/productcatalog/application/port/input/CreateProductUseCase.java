package com.paklog.productcatalog.application.port.input;

import com.paklog.productcatalog.application.command.CreateProductCommand;
import com.paklog.productcatalog.domain.model.Product;

public interface CreateProductUseCase {
    Product createProduct(CreateProductCommand command);
}