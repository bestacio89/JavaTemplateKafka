package org.Personal.Service;

import org.Personal.Domain.Mongo.Enums.EventType;
import org.Personal.Domain.Postgres.BusinessObjects.User;
import org.Personal.Exceptions.ResourceNotFoundException;
import org.Personal.Persistence.Postgres.UserRepository;
import org.Personal.Producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional.*;
import java.time.LocalDateTime;
import java.util.List;

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
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        producer.sendEvent(EventType.CREATION, savedUser);
        logger.info("User created successfully: {}", savedUser);
        return savedUser;
    }

    public User updateUser(Long userId, User userDetails) {
        return userRepository.findById(userId).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setUpdatedDate(LocalDateTime.now());
            User updatedUser = userRepository.save(user);
            producer.sendEvent(EventType.EDITION, updatedUser);
            logger.info("User updated successfully: {}", updatedUser);
            return updatedUser;
        }).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username " + username));
    }

    public List<User> getAllUsers(int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> pagedResult = userRepository.findAll(pageable);
        return pagedResult.getContent();
    }

    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id " + userId);
        }
        userRepository.deleteById(userId);
        logger.info("User deleted successfully with id: {}", userId);
    }

    public void deleteUserByUsername(String username) {
        User user = getUserByUsername(username);
        userRepository.delete(user);
        logger.info("User deleted successfully with username: {}", username);
    }
}
