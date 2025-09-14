package com.paklog.productcatalog.application.port.input;

import com.paklog.productcatalog.application.query.GetProductQuery;
import com.paklog.productcatalog.application.query.ListProductsQuery;
import com.paklog.productcatalog.domain.model.Product;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface GetProductUseCase {
    Optional<Product> getProduct(GetProductQuery query);
    Page<Product> listProducts(ListProductsQuery query);
}