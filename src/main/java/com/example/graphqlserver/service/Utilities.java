package com.example.graphqlserver.service;

import com.example.graphqlserver.model.UserPrincipal;
import com.example.graphqlserver.model.UserPrincipal.UserResponse;

public class Utilities {

    public static UserResponse mapToUserResponse(UserPrincipal user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt());
    }

    public Utilities() {
    }
}
