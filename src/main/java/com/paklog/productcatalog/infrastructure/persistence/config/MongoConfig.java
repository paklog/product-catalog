package com.paklog.productcatalog.infrastructure.persistence.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.paklog.productcatalog.infrastructure.persistence.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {
    
    @Override
    protected String getDatabaseName() {
        return "productcatalog";
    }
    
    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}