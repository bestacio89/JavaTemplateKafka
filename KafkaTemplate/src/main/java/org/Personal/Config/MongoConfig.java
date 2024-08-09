package org.Personal.Config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration("spring.event")
@EnableMongoRepositories(basePackages = "org.Personal.Persistence.Mongo")
public class MongoConfig extends AbstractMongoClientConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${event.datasource.database}")
    private String databaseName;

    @Value("${event.datasource.uri}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean
    public MongoClient mongoClient() {
        try {
            MongoClient mongoClient = MongoClients.create(mongoUri);
            logger.info("MongoClient created successfully with URI: {}", mongoUri);
            return mongoClient;
        } catch (Exception e) {
            logger.error("Error creating MongoClient with URI: {}", mongoUri, e);
            throw new RuntimeException("Failed to create MongoClient", e);
        }
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
