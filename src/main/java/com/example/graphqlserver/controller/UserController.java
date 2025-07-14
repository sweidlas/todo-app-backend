package com.example.graphqlserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.example.graphqlserver.model.UserPrincipal;
import com.example.graphqlserver.model.UserPrincipal.UpdateUserEmail;
import com.example.graphqlserver.model.UserPrincipal.UpdateUserPassword;
import com.example.graphqlserver.model.UserPrincipal.UserResponse;
import com.example.graphqlserver.model.UserSettings;
import com.example.graphqlserver.repository.UserSettingsRepository;
import com.example.graphqlserver.service.AuthenticationService;
import com.example.graphqlserver.service.JwtUtil;
import com.example.graphqlserver.service.UserService;

@Controller
public class UserController {

    @Autowired
    private JwtUtil jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @QueryMapping
    public UserResponse user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        return userService.findUserResponseById(user.getId());
    }

    @MutationMapping
    public UserResponse updateUserEmail(@Argument("input") UpdateUserEmail input) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(user);
        authenticationService.sendEmailVerificationEmail(user, input.email(), jwtToken);
        return userService.setUserEmailToVerify(input.email(), user.getId());
    }

    @MutationMapping
    public UserResponse updateUserPassword(@Argument("input") UpdateUserPassword input) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        user = authenticationService.checkPassword(user, input.oldPassword());

        return userService.updateUserPassword(input, user.getId());
    }

    @QueryMapping
    public UserSettings userSettings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        return userSettingsRepository.findByUserId(user.getId())
                .orElse(UserSettings.createDefault(user.getId()));
    }

    @SchemaMapping(typeName = "User")
    public UserSettings userSettings(UserResponse user) {
        return userSettingsRepository.findByUserId(user.id())
                .orElse(UserSettings.createDefault(user.id()));
    }

}
