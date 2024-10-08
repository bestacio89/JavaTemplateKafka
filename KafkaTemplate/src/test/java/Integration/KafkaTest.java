package Integration;

import Config.KafkaTestConfig;
import org.Personal.Consumer.Consumer;
import org.Personal.Main;
import org.Personal.Producer.Producer;
import org.Personal.Domain.Mongo.Entities.Event;
import org.Personal.Domain.Mongo.Enums.EventType;
import org.Personal.Domain.Postgres.BusinessObjects.User;
import org.Personal.Service.BusinessRebuildService;
import org.Personal.Utility.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {KafkaTestConfig.class, Main.class},
        properties = "spring.main.allow-bean-definition-overriding=true",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class KafkaTest {

    @Autowired
    private Producer producer;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private BusinessRebuildService businessRebuildService;

    @Autowired
    private CountDownLatch countDownLatch;

    @Autowired
    private Consumer consumer;

    @BeforeEach
    void setUp() {
        // Re-initialize the Consumer with the mocked BusinessRebuildService
        consumer = new Consumer(businessRebuildService);

        // Reset the mock for each test
        reset(businessRebuildService);
    }

    @Test
    void testSendAndReceiveEvent() throws Exception {
        // Prepare the User object
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setCreatedAt(LocalDateTime.now());

        // Prepare the event
        Event event = new Event();
        event.setType(EventType.CREATION);
        event.setDescription(JsonUtil.serialize(user));
        event.setTimestamp(LocalDateTime.now());

        // Send the event
        producer.sendEvent(EventType.CREATION, user);

        // Wait for the message to be received
        boolean messageReceived = countDownLatch.await(10, TimeUnit.SECONDS); // Adjust timeout as needed
        assertTrue(messageReceived, "Message was not received by the consumer");

        // Verify that the businessRebuildService handled the event
        verify(businessRebuildService, times(1)).handleEvent(any(Event.class));

        // Optionally, verify the content
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(businessRebuildService).handleEvent(eventCaptor.capture());
        Event receivedEvent = eventCaptor.getValue();

        assertEquals(event.getType(), receivedEvent.getType());
        assertEquals(event.getDescription(), receivedEvent.getDescription());
    }

    @Test
    void testEmptyMessage() throws Exception {
        // Send an empty message
        producer.sendEvent(EventType.CREATION, new User());

        // Wait for the message to be received
        boolean messageReceived = countDownLatch.await(10, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Message was not received by the consumer");

        // Verify that the businessRebuildService handled the event
        verify(businessRebuildService, times(1)).handleEvent(any(Event.class));

        // Optionally, verify the content
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(businessRebuildService).handleEvent(eventCaptor.capture());
        Event receivedEvent = eventCaptor.getValue();

        assertEquals(EventType.CREATION, receivedEvent.getType());
        assertNotNull(receivedEvent.getDescription()); // Check if description is not null
    }

    @Test
    void testInvalidMessage() throws Exception {
        // Send an invalid message (e.g., corrupted JSON)
        String invalidMessage = "{ \"type\": \"INVALID_TYPE\", \"description\": \"Invalid message\", \"timestamp\": \"Not a date\" }";

        // Send the invalid message directly to the Kafka topic
        kafkaTemplate.send("test-topic", invalidMessage);

        // Wait for the message to be processed
        boolean messageReceived = countDownLatch.await(10, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Message was not received by the consumer");

        // Verify that the businessRebuildService did not handle an invalid event
        verify(businessRebuildService, never()).handleEvent(any(Event.class));
    }

    @Test
    void testMessageHandlingError() throws Exception {
        // Configure the Consumer to throw an exception
        doThrow(new RuntimeException("Simulated error")).when(businessRebuildService).handleEvent(any(Event.class));

        // Prepare the User object
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setCreatedAt(LocalDateTime.now());

        // Send the event
        producer.sendEvent(EventType.CREATION, user);

        // Wait for the message to be processed
        boolean messageReceived = countDownLatch.await(10, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Message was not received by the consumer");

        // Verify that the businessRebuildService attempted to handle the event
        verify(businessRebuildService, times(1)).handleEvent(any(Event.class));
    }

    @Test
    void testMultipleMessages() throws Exception {
        // Prepare the User objects
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        // Send multiple events
        producer.sendEvent(EventType.CREATION, user1);
        producer.sendEvent(EventType.CREATION, user2);

        // Wait for messages to be received
        boolean messagesReceived = countDownLatch.await(10, TimeUnit.SECONDS);
        assertTrue(messagesReceived, "Messages were not received by the consumer");

        // Verify that the businessRebuildService handled the events
        verify(businessRebuildService, times(2)).handleEvent(any(Event.class));

        // Optionally, verify the content
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(businessRebuildService, times(2)).handleEvent(eventCaptor.capture());
        assertEquals(2, eventCaptor.getAllValues().size());
    }
}
