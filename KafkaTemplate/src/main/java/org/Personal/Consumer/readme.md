# `org.Personal.Consumer` Package

This package contains the Kafka consumer configuration for receiving and processing events. The main component is a Kafka consumer that listens to messages from a specified Kafka topic, deserializes them, and processes them using a service.

## Class Overview

### 1. `Consumer`

The `Consumer` class is responsible for consuming messages from a Kafka topic, deserializing them into `Event` objects, and delegating the processing of these events to a `BusinessRebuildService`.

#### Key Components:

- **BusinessRebuildService**: A service that handles the processing of events. The `Consumer` class uses this service to manage the events after they are deserialized.
- **JsonUtil**: A utility class used for serializing and deserializing JSON data. This is used to convert the Kafka message into an `Event` object.
- **KafkaListener**: The annotation used to designate the method that should listen to Kafka messages. It specifies the topic and group ID for the consumer.

#### Configuration Details:

- **Kafka Topic**: The consumer listens to the `"test-topic"` Kafka topic.
- **Group ID**: The consumer is part of the `"my-group"` consumer group.
- **Message Handling**: The `listen` method is triggered when a message is received. The message is deserialized into an `Event` object, which is then processed by the `BusinessRebuildService`.

#### Example:

```java
@Component
public class Consumer {

    private final BusinessRebuildService eventReplayService;

    public Consumer(BusinessRebuildService eventReplayService) {
        this.eventReplayService = eventReplayService;
    }

    @KafkaListener(topics = "test-topic", groupId = "my-group")
    public void listen(String message) {
        Event event = JsonUtil.deserialize(message, Event.class);
        eventReplayService.handleEvent(event);
        System.out.println("Received event in consumer: " + event);
    }
}
```

#### How It Works:

1. **Kafka Listener**: The `@KafkaListener` annotation marks the `listen` method to be invoked when a message is published to the `"test-topic"` topic.
2. **Deserialization**: The `JsonUtil.deserialize` method converts the message (which is a JSON string) into an `Event` object.
3. **Event Processing**: The deserialized `Event` object is passed to the `eventReplayService.handleEvent` method for processing.
4. **Logging**: The received event is logged to the console for debugging and tracking purposes.

## Summary

The `org.Personal.Consumer` package is designed to handle the consumption of Kafka messages. The `Consumer` class listens to a Kafka topic, deserializes incoming messages into `Event` objects, and processes them using a designated service. This setup allows the application to react to and process events in a scalable and asynchronous manner.

---

.