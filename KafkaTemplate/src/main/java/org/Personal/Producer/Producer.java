package org.Personal.Producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.Personal.Domain.Mongo.Entities.Event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public Producer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(Event event) {
        try {
            // Convert Event to JSON string
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("event-topic", message);
            System.out.println("Sent event: " + message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
