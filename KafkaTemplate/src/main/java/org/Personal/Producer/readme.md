# `org.Personal.Producer` Package

The `org.Personal.Producer` package is responsible for producing and sending messages to Kafka topics. The primary class in this package is the `Producer`, which handles the creation and sending of `Event` objects to Kafka. This package plays a crucial role in integrating with Kafka for event-driven architecture and ensuring that events are properly sent to the message broker.

## `Producer` Class

The `Producer` class is responsible for creating and sending events to a Kafka topic. It uses the `KafkaTemplate` provided by Spring Kafka to interact with Kafka brokers. The class encapsulates the logic for event creation and serialization, ensuring that events are correctly formatted and transmitted.

### Class Definition

```java
package org.Personal.Producer;

import org.Personal.Domain.Mongo.Entities.Event;
import org.Personal.Domain.Mongo.Enums.EventType;
import org.Personal.Domain.Postgres.BusinessObjects.User;
import org.Personal.Utility.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Producer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(EventType type, User user) {
        Event event = new Event();
        event.setType(type);
        event.setDescription(JsonUtil.serialize(user));
        event.setTimestamp(LocalDateTime.now());

        kafkaTemplate.send("test-topic", JsonUtil.serialize(event));
        System.out.println("Sent event: " + event);
    }
}
```

### Key Methods

- **`sendEvent(EventType type, User user)`**: This method is used to create an `Event` object with the given `EventType` and `User`. It serializes the `User` object to JSON and sets it as the description of the `Event`. The event is then sent to the Kafka topic `"test-topic"`. This method also logs the event that was sent for debugging and traceability purposes.

### Dependencies and Components

- **`KafkaTemplate<String, String>`**: This Spring Kafka component is used to send messages to Kafka. It abstracts the complexity of interacting with Kafka producers and provides a high-level API for sending messages.

- **`JsonUtil`**: A utility class responsible for serializing and deserializing objects to and from JSON. This class ensures that objects are correctly converted to a format that Kafka can handle.

- **`Event`**: A domain model representing an event to be sent to Kafka. It includes attributes such as `type`, `description`, and `timestamp`.

- **`EventType`**: An enumeration representing different types of events. This ensures that only predefined types are used, reducing the risk of errors.

- **`User`**: A domain model representing a user in the system. This object is serialized and included in the event's description.

### Purpose and Benefits

1. **Event Creation and Sending**: The `Producer` class abstracts the details of creating and sending events to Kafka, allowing other parts of the application to focus on business logic rather than message handling.

2. **Decoupling**: By using Kafka for event messaging, the application components are decoupled, promoting scalability and flexibility in the system architecture.

3. **Logging**: The `sendEvent` method includes a logging statement that outputs the sent event, aiding in debugging and monitoring.

4. **Serialization**: The `JsonUtil` class provides a consistent approach to JSON serialization, ensuring that the event data is correctly formatted for Kafka.

### Usage

To use the `Producer` class, inject it into your service or component where you need to send events. Call the `sendEvent` method with the appropriate `EventType` and `User` object.

Example:

```java
@Service
public class UserService {

    private final Producer producer;

    @Autowired
    public UserService(Producer producer) {
        this.producer = producer;
    }

    public void createUser(User user) {
        // Business logic for creating a user
        producer.sendEvent(EventType.CREATION, user);
    }
}
```

In this example, the `UserService` class uses the `Producer` to send an event whenever a new user is created.

## Summary

The `org.Personal.Producer` package provides essential functionality for producing and sending events to Kafka. The `Producer` class encapsulates the logic for creating events, serializing them, and sending them to Kafka topics. This package supports the event-driven architecture of the application and ensures smooth integration with Kafka for messaging and event handling.

---

