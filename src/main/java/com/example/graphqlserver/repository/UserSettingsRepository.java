package com.example.graphqlserver.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.graphqlserver.model.UserSettings;

@Repository
public interface UserSettingsRepository extends MongoRepository<UserSettings, String> {
    Optional<UserSettings> findByUserId(String userId);
}