package org.Personal.Service;

import org.Personal.Domain.Mongo.Entities.Event;
import org.Personal.Domain.Mongo.Enums.EventType;
import org.Personal.Domain.Postgres.BusinessObjects.User;
import org.Personal.Persistence.Postgres.UserRepository;
import org.Personal.Producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final Producer producer;

    @Autowired
    public UserService(UserRepository userRepository, Producer producer) {
        this.userRepository = userRepository;
        this.producer = producer;
    }

    public User createUser(User user) {
        try {
            user.setCreatedAt(LocalDateTime.now());
            User savedUser = userRepository.save(user);

            // Publish an event
            producer.sendEvent(EventType.CREATION, savedUser);

            logger.info("User created successfully: {}", savedUser);
            return savedUser;
        } catch (Exception e) {
            logger.error("Error creating user: {}", user, e);
            throw e; // Optionally rethrow or handle differently
        }
    }

    public void deleteUser(Long userId) {
        try {
            User userToDelete = userRepository.findById(userId).orElse(null);
            if (userToDelete != null) {
                userRepository.deleteById(userId);
                // Publish an event
                producer.sendEvent(EventType.DELETION, userToDelete);
                logger.info("User deleted successfully: {}", userToDelete);
            } else {
                logger.warn("No user found with id '{}'", userId);
            }
        } catch (Exception e) {
            logger.error("Error deleting user with id '{}'", userId, e);
            throw e; // Optionally rethrow or handle differently
        }
    }

    public User getUserByUsername(String username) {
        try {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                logger.info("User found by username '{}': {}", username, user);
            } else {
                logger.warn("No user found with username '{}'", username);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error retrieving user by username '{}'", username, e);
            throw e; // Optionally rethrow or handle differently
        }
    }
}
