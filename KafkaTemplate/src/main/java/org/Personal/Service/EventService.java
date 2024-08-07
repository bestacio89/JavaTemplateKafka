package org.Personal.Service;




import org.Personal.Domain.Mongo.Entities.Event;
import org.Personal.Persistence.Mongo.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;



    public Event getEventByName(String eventName) {
        return eventRepository.findByEventName(eventName);
    }

    public List<Event> getEventsUpTo(LocalDateTime timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }

        List<Event> events = eventRepository.findByTimestampBefore(timestamp);

        if (events.isEmpty()) {
            // Optionally log or handle the case when no events are found
            return Collections.emptyList();
        }

        return events;
    }



    public Event createEvent(Event event) {
        event.setCreatedDate(LocalDateTime.now());
        event.setUpdatedDate(LocalDateTime.now());
        event.setDeleted(false);
        return eventRepository.save(event);
    }

    public Event updateEvent(String id, Event updatedEvent) {
        Event existingEvent = eventRepository.findById(id).orElse(null);
        if (existingEvent != null) {
            existingEvent.setType(updatedEvent.getType());
            existingEvent.setDescription(updatedEvent.getDescription());
            existingEvent.setTimestamp(updatedEvent.getTimestamp());
            existingEvent.setUpdatedDate(LocalDateTime.now());
            return eventRepository.save(existingEvent);
        }
        return null;
    }

    public Event getEventById(String id) {
        return eventRepository.findById(id).orElse(null);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public void deleteEventById(String id) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event != null) {
            event.setDeleted(true);
            event.setUpdatedDate(LocalDateTime.now());
            eventRepository.save(event);
        }
    }
}
