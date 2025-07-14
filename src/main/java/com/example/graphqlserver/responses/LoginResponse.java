package com.example.graphqlserver.responses;

import com.example.graphqlserver.model.UserPrincipal.UserResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private long expiresIn;
    private UserResponse user;

    public LoginResponse(String token, long expiresIn, UserResponse user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }
}
