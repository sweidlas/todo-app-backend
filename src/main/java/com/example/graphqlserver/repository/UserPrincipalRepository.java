package com.example.graphqlserver.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.graphqlserver.model.UserPrincipal;

@Repository
public interface UserPrincipalRepository extends MongoRepository<UserPrincipal, String> {
    Optional<UserPrincipal> findByUsername(String username);

    Optional<UserPrincipal> findByEmail(String email);

    Optional<UserPrincipal> findByVerificationCode(String verificationCode);

    boolean existsByUsername(String username);
}
