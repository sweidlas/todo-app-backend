package com.example.graphqlserver.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.graphqlserver.model.Todo;

public interface TodoRepository extends MongoRepository<Todo, String> {

    Optional<Todo> findTodoById(String id); // mongoDB defines the method itself @todo check if userId is correct

    List<Todo> findTodoByUserId(String userId);

    List<Todo> findByUserIdAndCompleted(String userId, boolean completed);

}