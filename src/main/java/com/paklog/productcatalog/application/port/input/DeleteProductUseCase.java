package com.paklog.productcatalog.application.port.input;

import com.paklog.productcatalog.application.command.DeleteProductCommand;
import jakarta.validation.Valid;

public interface DeleteProductUseCase {
    boolean deleteProduct(@Valid DeleteProductCommand command);
}