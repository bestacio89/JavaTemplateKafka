# `org.Personal.Config` Package

This package contains configuration classes for setting up Kafka, MongoDB, and PostgreSQL in a Spring Boot application. Each class in this package provides essential configurations required to integrate and manage these technologies within the application.

## Class Overview

### 1. `KafkaConfig`

Configures Kafka producer and consumer settings, including serialization and deserialization mechanisms, and Kafka listener container.

#### Key Components:

- **ProducerFactory**: Configures the producer properties, including bootstrap servers and serializers.
- **KafkaTemplate**: Provides the Kafka template for sending messages to Kafka topics.
- **ConsumerFactory**: Configures the consumer properties, including bootstrap servers, group ID, and deserializers.
- **ConcurrentKafkaListenerContainerFactory**: Configures the Kafka listener container factory for processing messages.

#### Configuration Details:

- **Bootstrap Servers**: Configured to connect to `localhost:9092`. Update this as needed for your Kafka setup.
- **Serialization**: Uses `StringSerializer` for keys and `JsonSerializer` for values.
- **Deserialization**: Uses `ErrorHandlingDeserializer` and `JsonDeserializer` for handling message deserialization.
- **Trusted Packages**: Configured using `TrustedPackages.PACKAGES` to ensure only trusted packages are deserialized.

#### Example:

```java
@Bean
public ProducerFactory<String, String> producerFactory() {
    // Configuration details
}

@Bean
public KafkaTemplate<String, String> kafkaTemplate() {
    // Configuration details
}

@Bean
public ConsumerFactory<String, String> consumerFactory() {
    // Configuration details
}

@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
    // Configuration details
}
```

### 2. `MongoConfig`

Configures MongoDB client and repository settings, including connection details and database name.

#### Key Components:

- **MongoClient**: Creates a MongoDB client with the specified URI.
- **AbstractMongoClientConfiguration**: Provides database configuration including auto-index creation.

#### Configuration Details:

- **Database Name**: Configured via `event.datasource.database` property.
- **Mongo URI**: Configured via `event.datasource.uri` property for connecting to MongoDB.

#### Example:

```java
@Bean
public MongoClient mongoClient() {
    // Configuration details
}

@Override
protected String getDatabaseName() {
    // Database name
}

@Override
protected boolean autoIndexCreation() {
    // Index creation setting
}
```

### 3. `PostgresConfig`

Configures PostgreSQL datasource, entity manager factory, and transaction management for the application.

#### Key Components:

- **DataSourceProperties**: Configures PostgreSQL datasource properties.
- **HikariDataSource**: Uses HikariCP for connection pooling.
- **LocalContainerEntityManagerFactoryBean**: Configures the entity manager factory for JPA.
- **JpaTransactionManager**: Configures the transaction manager for JPA transactions.

#### Configuration Details:

- **DataSource**: Configured using properties prefixed with `spring.datasource.businessdatasource`.
- **Entity Manager Factory**: Configures JPA with PostgreSQL.
- **Transaction Manager**: Manages JPA transactions.

#### Example:

```java
@Bean(name = "businessDataSourceProperties")
@ConfigurationProperties(prefix = "spring.datasource.businessdatasource")
public DataSourceProperties dataSourceProperties() {
    // Configuration details
}

@Bean(name = "businessDataSource")
public HikariDataSource dataSource(@Qualifier("businessDataSourceProperties") DataSourceProperties dataSourceProperties) {
    // Configuration details
}

@Bean(name = "postgresEntityManagerFactory")
public LocalContainerEntityManagerFactoryBean entityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("businessDataSource") DataSource dataSource) {
    // Configuration details
}

@Bean(name = "postgresTransactionManager")
public JpaTransactionManager transactionManager(
        @Qualifier("postgresEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    // Configuration details
}
```

## Summary

The `org.Personal.Config` package provides essential configurations for integrating Kafka, MongoDB, and PostgreSQL into a Spring Boot application. By defining producer and consumer settings for Kafka, connection details for MongoDB, and datasource and JPA settings for PostgreSQL, this package ensures that the application can effectively communicate with and utilize these technologies.

---

