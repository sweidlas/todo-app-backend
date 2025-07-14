package com.example.graphqlserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userSettings")
public record UserSettings(
        @Id String id,
        @Indexed String userId,
        boolean showAnimations) {
    // You can add static factory methods for convenience
    public static UserSettings createDefault(String userId) {
        return new UserSettings(
                null, // MongoDB will generate the ID
                userId,
                true);
    }

}
