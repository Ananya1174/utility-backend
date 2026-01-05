package com.utility.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.utility.auth.model.PasswordResetToken;

public interface PasswordResetTokenRepository
        extends MongoRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findByToken(String token);
    List<PasswordResetToken> findByEmailAndUsedFalse(String email);
}