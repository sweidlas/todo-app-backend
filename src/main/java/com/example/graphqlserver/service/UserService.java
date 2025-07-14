package com.example.graphqlserver.service;

import static com.example.graphqlserver.service.Utilities.mapToUserResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.graphqlserver.exceptions.CustomException;
import com.example.graphqlserver.exceptions.ErrorCode;
import com.example.graphqlserver.model.UserPrincipal;
import com.example.graphqlserver.model.UserPrincipal.UpdateUserPassword;
import com.example.graphqlserver.model.UserPrincipal.UserResponse;
import com.example.graphqlserver.repository.UserPrincipalRepository;

@Service
public class UserService {
    @Autowired
    private UserPrincipalRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse findUserResponseById(String id) {
        UserPrincipal user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    public UserResponse setUserEmailToVerify(String newEmail, String id) {
        UserPrincipal user = userRepository.findById(id).map(existingUser -> {
            // Create a new UserPrincipal with updated email while preserving unmodified
            // fields
            UserPrincipal updatedUser = new UserPrincipal(
                    existingUser.getUsername(),
                    existingUser.getEmail(),
                    passwordEncoder.encode(existingUser.getPassword()),
                    existingUser.getAuthorities(),
                    newEmail);
            updatedUser.setId(existingUser.getId());
            updatedUser.setCreatedAt(existingUser.getCreatedAt());
            updatedUser.setUpdatedAt(java.time.LocalDateTime.now());
            updatedUser.setEnabled(true);

            // Save and return the updated UserPrincipal
            return userRepository.save(updatedUser);
        })
                // .orElseThrow(() -> new IllegalArgumentException("Todo not found with id: " +
                // input.id()));
                .orElse(null);
        return mapToUserResponse(user);
    }

    public UserResponse setUserEmailAfterVerified(String id) {
        UserPrincipal user = userRepository.findById(id).map(existingUser -> {
            // Create a new UserPrincipal with updated email while preserving unmodified
            // fields
            if (existingUser.getEmailToVerify() != null) {
                UserPrincipal updatedUser = new UserPrincipal(
                        existingUser.getUsername(),
                        existingUser.getEmailToVerify(),
                        passwordEncoder.encode(existingUser.getPassword()),
                        existingUser.getAuthorities(),
                        null);
                updatedUser.setId(existingUser.getId());
                updatedUser.setCreatedAt(existingUser.getCreatedAt());
                updatedUser.setUpdatedAt(java.time.LocalDateTime.now());
                updatedUser.setEnabled(true);

                // Save and return the updated UserPrincipal
                return userRepository.save(updatedUser);
            } else {
                throw new CustomException(ErrorCode.NO_EMAIL_TO_VERIFY);
            }
        })
                // .orElseThrow(() -> new IllegalArgumentException("Todo not found with id: " +
                // input.id()));
                .orElse(null);
        return mapToUserResponse(user);
    }

    public UserResponse updateUserPassword(UpdateUserPassword input, String id) {
        UserPrincipal user = userRepository.findById(id).map(existingUser -> {
            // Create a new UserPrincipal with updated password while preserving unmodified
            // fields
            UserPrincipal updatedUser = new UserPrincipal(
                    existingUser.getUsername(),
                    existingUser.getEmail(),
                    passwordEncoder.encode(input.newPassword()),
                    existingUser.getAuthorities(),
                    null);
            updatedUser.setId(existingUser.getId());
            updatedUser.setCreatedAt(existingUser.getCreatedAt());
            updatedUser.setUpdatedAt(java.time.LocalDateTime.now());
            updatedUser.setEnabled(true);

            // Save and return the updated UserPrincipal
            return userRepository.save(updatedUser);
        })
                .orElse(null);
        return mapToUserResponse(user);
    }
}
