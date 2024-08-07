package org.Personal.Persistence.Mongo;


import org.Personal.Domain.Mongo.Entities.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

     Event findByEventName(String eventName);
     List<Event> findByTimestampBefore(LocalDateTime dateTime) ;
     List<Event> getAllEvents ();
}