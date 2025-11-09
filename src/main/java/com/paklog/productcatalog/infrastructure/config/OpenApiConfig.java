package com.paklog.productcatalog.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
                        .version("1.1.0"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Local development server"),
                        new Server()
                                .url("https://api.example.com/v1")
                                .description("Example production server")
                ));
    }
}
