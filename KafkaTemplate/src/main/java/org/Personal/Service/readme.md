# `org.Personal.Service` Package

The `org.Personal.Service` package contains service classes responsible for business logic and interactions with the repositories and Kafka producer. Each service class plays a specific role in the application’s architecture, handling user management, event management, and event processing.

## Service Classes

### 1. `BusinessRebuildService`

**Purpose**: The `BusinessRebuildService` class is responsible for rebuilding the state of the application based on historical events. It retrieves events from the `EventService`, processes them, and updates the state of the business objects in the `UserRepository`.

**Key Methods**:

- **`rebuildFromEvents()`**: Clears the current state of users in the repository and then processes all events to recreate the state based on the event history.
- **`handleEvent(Event event)`**: Processes individual events based on their type (creation, deletion, edition, etc.) and performs the appropriate actions on the `UserRepository`.

**Usage**: This service is typically used for replaying events to restore the application’s state after a restart or for initializing data from historical events.

**Example**:
```java
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
                User createdUser = JsonUtil.deserialize(event.getDescription(), User.class);
                userRepository.save(createdUser);
                break;
            case DELETION:
                User deletedUser = JsonUtil.deserialize(event.getDescription(), User.class);
                userRepository.delete(deletedUser);
                break;
            case EDITION:
                User editedUser = JsonUtil.deserialize(event.getDescription(), User.class);
                userRepository.save(editedUser);
                break;
            case INTEGRATION:
                // Handle integration event
                break;
            default:
                throw new IllegalArgumentException("Unknown event type: " + event.getType());
        }
    }
}
```

### 2. `EventService`

**Purpose**: The `EventService` class manages CRUD operations for `Event` entities. It interacts with the `EventRepository` to handle event retrieval, creation, updating, and deletion.

**Key Methods**:

- **`getEventByName(String eventName)`**: Retrieves an event by its name.
- **`getEventsUpTo(LocalDateTime timestamp)`**: Retrieves events that occurred before a specified timestamp.
- **`createEvent(Event event)`**: Creates and saves a new event.
- **`updateEvent(String id, Event updatedEvent)`**: Updates an existing event.
- **`getEventById(String id)`**: Retrieves an event by its ID.
- **`getAllEvents()`**: Retrieves all events.
- **`deleteEventById(String id)`**: Marks an event as deleted.

**Usage**: This service is used for managing events within the application, including querying historical events and performing operations on event records.

**Example**:
```java
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
```

### 3. `UserService`

**Purpose**: The `UserService` class manages user-related operations, including creation, deletion, and retrieval of users. It interacts with the `UserRepository` and publishes events to Kafka when users are created or deleted.

**Key Methods**:

- **`createUser(User user)`**: Creates a new user, saves it to the repository, and publishes a creation event to Kafka.
- **`deleteUser(Long userId)`**: Deletes a user from the repository and publishes a deletion event to Kafka.
- **`getUserByUsername(String username)`**: Retrieves a user by their username.

**Usage**: This service is used for handling user management tasks and integrating with the Kafka producer to publish user-related events.

**Example**:
```java
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
```

## Summary

The `org.Personal.Service` package provides essential business logic for managing users and events. The `BusinessRebuildService` handles the reconstruction of application state from events, the `EventService` manages event records, and the `UserService` performs user management tasks and integrates with Kafka for event publishing. These services ensure that the application’s business logic is correctly implemented and that events are properly processed and propagated throughout the system.

---

