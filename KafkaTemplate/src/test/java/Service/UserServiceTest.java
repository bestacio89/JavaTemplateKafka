package Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Personal.Domain.Mongo.Entities.Event;
import org.Personal.Domain.Mongo.Enums.EventType;
import org.Personal.Domain.Postgres.BusinessObjects.User;
import org.Personal.Service.UserService;
import Config.KafkaTestConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ContextConfiguration(classes = {KafkaTestConfig.class})
@EmbeddedKafka(topics = {"event-topic"}, partitions = 1)
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private CountDownLatch countDownLatch;

    @Autowired
    private ConcurrentMessageListenerContainer<String, String> messageListenerContainer;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

    }

    @Test
    public void testUserCreationEvent() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        // Act
        userService.createUser(user);

        // Wait for the message to be consumed
        boolean messageReceived = countDownLatch.await(10, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Message was not received by the consumer");

        // Capture the event
        ArgumentCaptor<ConsumerRecord<String, String>> captor = ArgumentCaptor.forClass(ConsumerRecord.class);
        verify(messageListenerContainer).setupMessageListener(captor.capture());

        Event event = objectMapper.readValue(captor.getValue().value(), Event.class);

        // Assert
        assertEquals(EventType.CREATION, event.getType());
        assertTrue(event.getDescription().contains("User created with ID:"));
    }

    @Test
    public void testUserDeletionEvent() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user = userService.createUser(user);

        // Act
        userService.deleteUser(user.getId());

        // Wait for the message to be consumed
        boolean messageReceived = countDownLatch.await(10, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Message was not received by the consumer");

        // Capture the event
        ArgumentCaptor<ConsumerRecord<String, String>> captor = ArgumentCaptor.forClass(ConsumerRecord.class);
        verify(messageListenerContainer).setupMessageListener(captor.capture());

        Event event = objectMapper.readValue(captor.getValue().value(), Event.class);

        // Assert
        assertEquals(EventType.DELETION, event.getType());
        assertTrue(event.getDescription().contains("User deleted with ID:"));
    }
}
