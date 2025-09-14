package com.paklog.productcatalog.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI productCatalogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Catalog API")
                        .description("A RESTful API for managing product catalog data, including dimensions and compliance attributes. " +
                                   "This API is designed using Domain-Driven Design principles, with the Product as the central Aggregate.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Product Catalog Team")
                                .email("product-catalog@paklog.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.paklog.com/v1")
                                .description("Production server")
                ));
    }
}