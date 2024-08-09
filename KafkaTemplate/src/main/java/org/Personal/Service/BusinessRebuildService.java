package org.Personal.Service;


import org.Personal.Domain.Mongo.Entities.Event;
import org.Personal.Domain.Postgres.BusinessObjects.User;

import org.Personal.Persistence.Postgres.UserRepository;
import org.Personal.Utility.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BusinessRebuildService {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    public void rebuildFromEvents() {
        userRepository.deleteAll(); // Clear current state
        List<Event> events = eventService.getAllEvents();

        for (Event event : events) {
            handleEvent(event);
        }
    }

    public void handleEvent(Event event) {
        // Handle different types of events
        switch (event.getType()) {
            case CREATION:
                // Deserialize event description to User and save to repository
                User createdUser = JsonUtil.deserialize(event.getDescription(), User.class);
                userRepository.save(createdUser);
                break;
            case DELETION:
                // Find and delete the user
                User deletedUser = JsonUtil.deserialize(event.getDescription(), User.class);
                userRepository.delete(deletedUser);
                break;
            case EDITION:
                // Find and update the user
                User editedUser = JsonUtil.deserialize(event.getDescription(), User.class);
                userRepository.save(editedUser);
                break;
            case INTEGRATION:
                // Handle integration event
                // (implementation depends on integration specifics)
                break;
            default:
                throw new IllegalArgumentException("Unknown event type: " + event.getType());
        }
    }

}