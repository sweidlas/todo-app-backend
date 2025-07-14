package com.example.graphqlserver.model;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPrincipal extends User {

        String id;
        String email;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;

        private String verificationCode;
        private LocalDateTime verificationCodeExpiresAt;
        @Field("enabled2")
        private boolean enabled;
        String emailToVerify;

        public UserPrincipal(String username, String email, String password,
                        Collection<? extends GrantedAuthority> authorities, String emailToVerify) {
                super(username, password, authorities);
                this.id = null;
                this.email = email;
                this.createdAt = LocalDateTime.now();
                this.updatedAt = LocalDateTime.now();
                this.emailToVerify = emailToVerify;
        }


        public record CreateUserInput(
                        @Indexed(unique = true) String username,
                        String password,
                        String email) {
        }

        public record UpdateUserPassword(
                        String oldPassword,
                        String newPassword) {
        }

        public record UpdateUserEmail(
                        String email) {
        }

        public record UserResponse(
                        String id,
                        String username,
                        String email,
                        LocalDateTime createdAt) {
        }

        public enum Role {
                User,
                Admin
        }
}
