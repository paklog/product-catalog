package com.paklog.productcatalog.application.port.input;

import com.paklog.productcatalog.application.command.DeleteProductCommand;

public interface DeleteProductUseCase {
    boolean deleteProduct(DeleteProductCommand command);
}