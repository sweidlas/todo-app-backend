package com.example.graphqlserver.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.graphqlserver.model.CalendarEvent;

public interface CalendarEventRepository extends MongoRepository<CalendarEvent, String> {

    Optional<CalendarEvent> findCalendarEventById(String id); // mongoDB defines the method itself @todo check if userId
                                                              // is correct

    List<CalendarEvent> findCalendarEventByUserId(String userId);

}
