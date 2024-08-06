package org.Personal.Service;




import org.Personal.Domain.Mongo.Entities.Event;
import org.Personal.Persistence.Mongo.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event getEventByName(String eventName) {
        return eventRepository.findByEventName(eventName);
    }
}
