package org.Personal.Consumer;


import org.Personal.Domain.Mongo.Entities.Event;
import org.Personal.Service.BusinessRebuildService;
import org.Personal.Utility.JsonUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
