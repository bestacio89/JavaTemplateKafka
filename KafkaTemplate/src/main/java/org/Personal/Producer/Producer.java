package org.Personal.Producer;

import org.Personal.Domain.Mongo.Entities.Event;
import org.Personal.Domain.Mongo.Enums.EventType;
import org.Personal.Domain.Postgres.BusinessObjects.User;
import org.Personal.Utility.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(EventType type, User user) {
        try {
            Event event = new Event();
            event.setType(type);
            event.setDescription(JsonUtil.serialize(user));
            event.setTimestamp(LocalDateTime.now());

            String serializedEvent = JsonUtil.serialize(event);
            kafkaTemplate.send("test-topic", serializedEvent);
            logger.info("Sent event: {}", serializedEvent);
        } catch (Exception e) {
            logger.error("Error creating or sending event", e);
        }
    }
}
