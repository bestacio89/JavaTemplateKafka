package org.Personal.Persistence.Mongo;


import org.Personal.Domain.Mongo.Entities.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

     Event findByEventName(String eventName);
}