package com.example.graphqlserver.auth_dto;

public record RegisterUserDto(
        String email,
        String password,
        String username) {
}
