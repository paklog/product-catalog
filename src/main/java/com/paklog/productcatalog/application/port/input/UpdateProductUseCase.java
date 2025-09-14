package com.paklog.productcatalog.application.port.input;

import com.paklog.productcatalog.application.command.UpdateProductCommand;
import com.paklog.productcatalog.application.command.PatchProductCommand;
import com.paklog.productcatalog.domain.model.Product;

import java.util.Optional;

public interface UpdateProductUseCase {
    Optional<Product> updateProduct(UpdateProductCommand command);
    Optional<Product> patchProduct(PatchProductCommand command);
}