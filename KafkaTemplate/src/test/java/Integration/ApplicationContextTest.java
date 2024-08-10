package Integration;

import Config.KafkaTestConfig;
import org.Personal.Main;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {KafkaTestConfig.class})
@ActiveProfiles("test")
public class ApplicationContextTest {
    @Test
    void contextLoads() {
        // This test will automatically pass if the application context is loaded successfully
    }
}
