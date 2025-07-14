package com.example.graphqlserver.model;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;

@Document
public record Todo (
    @Id String id,
    String userId,
    String title,
    String description,
    Boolean completed,
    Priority priority,
    OffsetDateTime due,
    String color,
    OffsetDateTime created,
    OffsetDateTime updated) {

public record CreateTodoInput(
    String userId,
    String title,
    String description,
    Boolean completed,
    Priority priority,
    OffsetDateTime due,
    String color
) {}

public record UpdateTodoInput(
    @NotNull String id,
    String title,
    String description,
    Boolean completed,
    Priority priority,
    OffsetDateTime due,
    String color
) {}


public enum Priority {
    LOW,
    MEDIUM,
    HIGH
}
}
