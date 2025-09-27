package com.paklog.productcatalog.application.port.input;

import com.paklog.productcatalog.application.command.UpdateProductCommand;
import com.paklog.productcatalog.application.command.PatchProductCommand;
import com.paklog.productcatalog.domain.model.Product;
import jakarta.validation.Valid;

import java.util.Optional;

public interface UpdateProductUseCase {
    Optional<Product> updateProduct(@Valid UpdateProductCommand command);
    Optional<Product> patchProduct(@Valid PatchProductCommand command);
}