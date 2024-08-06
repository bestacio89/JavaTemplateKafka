package Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.Personal.Domain.Mongo.Entities.Event;
import org.Personal.Domain.Mongo.Enums.EventType;
import org.Personal.Service.UserService;

import Config.KafkaTestConfig;
import org.Personal.Domain.Postgres.BusinessObjects.User;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@EmbeddedKafka(topics = {"event-topic"}, partitions = 1)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private final CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void testUserCreationEvent() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        // Act
        userService.createUser(user);

        // Wait for the message to be consumed
        latch.await();

        // Assert
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "false", embeddedKafkaBroker);
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<String, String> consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromEmbeddedTopics(consumer, "event-topic");

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "event-topic");

        Event event = new ObjectMapper().readValue(record.value(), Event.class);

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
        latch.await();

        // Assert
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "false", embeddedKafkaBroker);
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<String, String> consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromEmbeddedTopics(consumer, "event-topic");

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "event-topic");

        Event event = new ObjectMapper().readValue(record.value(), Event.class);

        assertEquals(EventType.DELETION, event.getType());
        assertTrue(event.getDescription().contains("User deleted with ID:"));
    }
}