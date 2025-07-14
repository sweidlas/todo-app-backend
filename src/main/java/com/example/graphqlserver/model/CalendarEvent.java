package com.example.graphqlserver.model;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;

@Document
public record CalendarEvent(
                @Id String id,
                String userId,
                String title,
                OffsetDateTime start,
                OffsetDateTime end,
                String color,
                String description,
                String location,
                OffsetDateTime created,
                OffsetDateTime updated) {

        public record CreateCalendarEventInput(
                        String userId,
                        String title,
                        OffsetDateTime start,
                        OffsetDateTime end,
                        String color,
                        String description,
                        String location) {
        }

        public record UpdateCalendarEventInput(
                        @NotNull String id,
                        String title,
                        OffsetDateTime start,
                        OffsetDateTime end,
                        String color,
                        String description,
                        String location) {
        }

}