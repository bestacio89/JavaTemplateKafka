package Integration;

import org.Personal.Producer.Producer;
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
public class ProducerTest {

    @Autowired
    private Producer producer;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private CountDownLatch countDownLatch;

    @Test
    void testSendMessage() throws Exception {
        String testMessage = "Hello, Kafka!";

        // Send a message
        producer.sendMessage(testMessage);

        // Wait for the message to be received
        boolean messageReceived = countDownLatch.await(10, TimeUnit.SECONDS); // Adjust timeout as needed

        // Verify that the message was received
        assertTrue(messageReceived, "Message was not received by the consumer");
    }
}
