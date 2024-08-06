package org.Personal.Service;


import org.Personal.Domain.Mongo.Entities.Event;
import org.Personal.Domain.Mongo.Enums.EventType;
import org.Personal.Domain.Postgres.BusinessObjects.User;
import org.Personal.Persistence.Postgres.UserRepository;
import org.Personal.Producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Producer producer;

    @Autowired
    public UserService(UserRepository userRepository, Producer producer) {
        this.userRepository = userRepository;
        this.producer = producer;
    }

    public User createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Publish an event
        Event event = new Event();
        event.setType(EventType.CREATION);
        event.setDescription("User created with ID: " + savedUser.getId());
        event.setTimestamp(LocalDateTime.now());
        producer.sendMessage(event);

        return savedUser;
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);

        // Publish an event
        Event event = new Event();
        event.setType(EventType.DELETION);
        event.setDescription("User deleted with ID: " + userId);
        event.setTimestamp(LocalDateTime.now());
        producer.sendMessage(event);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
