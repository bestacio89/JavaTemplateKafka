package Integration;

import Config.KafkaTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ConsumerTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private CountDownLatch countDownLatch;

    @Test
    void testConsumeMessage() throws Exception {
        String testMessage = "Hello, Kafka!";

        // Send a message
        kafkaTemplate.send("test-topic", testMessage);

        // Wait for the message to be consumed
        boolean messageConsumed = countDownLatch.await(10, TimeUnit.SECONDS); // Adjust timeout as needed

        // Verify that the message was consumed
        assertTrue(messageConsumed, "Message was not consumed by the consumer");
    }
}
