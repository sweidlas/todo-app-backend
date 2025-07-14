package com.example.graphqlserver.auth_dto;

public record LoginUserDto(
        String email,
        String password) {
}
