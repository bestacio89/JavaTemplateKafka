package org.Personal.Service;

import org.Personal.Domain.Mongo.Entities.Event;
import org.Personal.Domain.Postgres.BusinessObjects.User;
import org.Personal.Persistence.Postgres.UserRepository;
import org.Personal.Utility.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessRebuildService {

    private static final Logger logger = LoggerFactory.getLogger(BusinessRebuildService.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    public void rebuildFromEvents() {
        try {
            userRepository.deleteAll(); // Clear current state
            List<Event> events = eventService.getAllEvents();

            for (Event event : events) {
                handleEvent(event);
            }
        } catch (Exception e) {
            logger.error("Error rebuilding from events", e);
        }
    }

    public void handleEvent(Event event) {
        try {
            switch (event.getType()) {
                case CREATION:
                    // Deserialize event description to User and save to repository
                    User createdUser = JsonUtil.deserialize(event.getDescription(), User.class);
                    userRepository.save(createdUser);
                    logger.info("User created: {}", createdUser);
                    break;
                case DELETION:
                    // Find and delete the user
                    User deletedUser = JsonUtil.deserialize(event.getDescription(), User.class);
                    userRepository.delete(deletedUser);
                    logger.info("User deleted: {}", deletedUser);
                    break;
                case EDITION:
                    // Find and update the user
                    User editedUser = JsonUtil.deserialize(event.getDescription(), User.class);
                    userRepository.save(editedUser);
                    logger.info("User updated: {}", editedUser);
                    break;
                case INTEGRATION:
                    // Handle integration event
                    // (implementation depends on integration specifics)
                    logger.info("Integration event handled: {}", event);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown event type: " + event.getType());
            }
        } catch (Exception e) {
            logger.error("Error handling event: {}", event, e);
        }
    }
}
