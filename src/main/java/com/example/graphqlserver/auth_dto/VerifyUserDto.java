package com.example.graphqlserver.auth_dto;

public record VerifyUserDto(
        String email,
        String verificationCode) {
}
