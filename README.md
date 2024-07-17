### README

# Kafka Spring Boot Template

This repository provides a base configuration for a Spring Boot microservice integrated with Kafka, Elasticsearch, Sleuth, Zipkin, and Hibernate. This template serves as a starting point for developing a distributed microservice architecture, with pre-configured dependencies and basic setup for logging, tracing, and database interaction.

## Features

- **Kafka** for message-driven architecture.
- **Elasticsearch** for efficient search and analytics.
- **Sleuth and Zipkin** for distributed tracing.
- **Hibernate (JPA)** for ORM with MySQL.
- **Logstash** for logging configuration.

## Getting Started

### Prerequisites

- Java 17
- Maven
- Kafka
- Elasticsearch
- MySQL (or another database)
- Logstash
- Kibana

### Project Structure

- `src/main/java/org/personal/kafkatemplate`
  - `config`: Contains configuration classes.
  - `consumer`: Contains Kafka consumer classes.
  - `producer`: Contains Kafka producer classes.
  - `service`: Contains service classes where business logic and projections are implemented.
  - `utility`: Contains utility classes.
  - `controller`: Contains REST controllers.
  - `model`: Contains entity classes.
  - `repository`: Contains repository interfaces.

### Configuration Files

#### `application.yml`

This file contains the base configuration for the application. It includes configurations for Kafka, Hibernate, Elasticsearch, Sleuth, Zipkin, and logging.

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: your-group-id
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

  jpa:
    hibernate:
      ddl-auto: update
      dialect: org.hibernate.dialect.MySQLDialect
      show_sql: true
      format_sql: true

  datasource:
    url: jdbc:mysql://localhost:3306/mydatabase
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver

  data:
    elasticsearch:
      repositories:
        enabled: true
      cluster-name: my-cluster
      cluster-nodes: localhost:9300

  sleuth:
    sampler:
      probability: 1.0
  zipkin:
    base-url: http://localhost:9411/
    enabled: true

logging:
  level:
    org:
      springframework: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"

management:
  endpoints:
    web:
      exposure:
        include: "*"
  tracing:
    sampling:
      probability: 1.0
```

### Adapting the Configuration

This template provides a base configuration that you must adapt to your environment and requirements.

#### Kafka

Update the `bootstrap-servers`, `group-id`, and serializers/deserializers based on your Kafka setup.

#### Hibernate (JPA)

Modify the `datasource` configuration to match your database credentials and URL.

#### Elasticsearch

Update the `cluster-name` and `cluster-nodes` to connect to your Elasticsearch instance.

#### Sleuth and Zipkin

Ensure the `base-url` for Zipkin points to your Zipkin server.

### Logging Configuration

Ensure the `logback-spring.xml` is correctly configured for Logstash:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
    <destination>localhost:5000</destination>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
  </appender>
  <root level="INFO">
    <appender-ref ref="LOGSTASH"/>
  </root>
</configuration>
```

### Logstash Configuration

Create a `logstash.conf` file with the following configuration:

```conf
input {
  tcp {
    port => 5000
    codec => json
  }
}

output {
  elasticsearch {
    hosts => ["localhost:9200"]
  }
  stdout { codec => rubydebug }
}
```

### Repository and Service Classes

- **Repository Classes**: All repository classes should extend `JpaRepository` to provide CRUD operations and query methods.

  ```java
  import org.springframework.data.jpa.repository.JpaRepository;

  public interface MyEntityRepository extends JpaRepository<MyEntity, Long> {
      // Custom query methods (if any)
  }
  ```

- **Service Classes**: Service classes should contain business logic and projections.

  ```java
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Transactional;

  @Service
  @Transactional
  public class MyEntityService {
      
      private final MyEntityRepository repository;

      @Autowired
      public MyEntityService(MyEntityRepository repository) {
          this.repository = repository;
      }

      // Business logic and projections
  }
  ```

## Running the Application

1. Ensure Kafka, Elasticsearch, Logstash, and the proper Database Engine are running and properly configured.
2. Update `application.yml` with your specific configurations.
3. Build and run the Spring Boot application:

   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

## Conclusion

This template sets up a basic Spring Boot microservice with essential configurations. Adapt the provided configurations to suit your specific needs, and extend the project by adding your business logic, entities, and other components.

For more information, refer to the official documentation of the respective technologies used in this template.
