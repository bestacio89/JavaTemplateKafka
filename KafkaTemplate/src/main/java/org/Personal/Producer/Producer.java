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
