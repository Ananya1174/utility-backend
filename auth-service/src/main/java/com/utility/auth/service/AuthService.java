package com.utility.auth.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.authentication.BadCredentialsException;


import com.utility.auth.dto.response.LoginResponseDto;
import com.utility.auth.dto.response.UserResponseDto;
import com.utility.auth.event.NotificationPublisher;
import com.utility.auth.exception.ResourceNotFoundException;
import com.utility.auth.exception.UserAlreadyExistsException;
import com.utility.auth.model.PasswordResetToken;
import com.utility.auth.model.User;
import com.utility.auth.repository.PasswordResetTokenRepository;
import com.utility.auth.repository.UserRepository;
import com.utility.auth.security.JwtUtil;
import com.utility.auth.security.PasswordPolicyValidator;
import com.utility.common.dto.event.PasswordResetEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final NotificationPublisher notificationPublisher;

    // ---------------- REGISTER (ADMIN ONLY) ----------------
    public User registerUser(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(true);

        return userRepository.save(user);
    }

    // ---------------- LOGIN ----------------
    public LoginResponseDto login(String username, String password) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        User user = userRepository.findByUsername(username)
        		.orElseThrow(() -> new BadCredentialsException("Invalid credentials"));


        String token = jwtUtil.generateToken(
                user.getUserId(),             
                user.getUsername(),     
                user.getRole().name()
        );

        return new LoginResponseDto(
                token,
                "Bearer",
                user.getRole().name(),
                user.isPasswordChangeRequired()
        );
    }

    // ---------------- FORGOT PASSWORD ----------------
    public void forgotPassword(String email) {

        userRepository.findByEmail(email).ifPresent(user -> {
            List<PasswordResetToken> oldTokens =
                    passwordResetTokenRepository.findByEmailAndUsedFalse(email);

            oldTokens.forEach(t -> t.setUsed(true));
            passwordResetTokenRepository.saveAll(oldTokens);

            String token = UUID.randomUUID().toString();

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .email(email)
                    .token(token)
                    .expiryDate(LocalDateTime.now().plusMinutes(15))
                    .used(false)
                    .build();

            passwordResetTokenRepository.save(resetToken);

            PasswordResetEvent event = new PasswordResetEvent();
            event.setEmail(email);
            event.setResetToken(token);

            notificationPublisher.publishPasswordReset(event);
        });
    }
    // ---------------- RESET PASSWORD ----------------
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Invalid or expired reset token"));

        if (Boolean.TRUE.equals(resetToken.getUsed())
                || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        PasswordPolicyValidator.validate(newPassword);

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangeRequired(false);   
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    public void changePassword(String userId, String oldPassword, String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
        	throw new ResponseStatusException(
        	        HttpStatus.BAD_REQUEST,
        	        "Old password is incorrect"
        	    );
        }

        PasswordPolicyValidator.validate(newPassword);
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException(
                "New password must be different from old password"
            );
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangeRequired(false);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    public List<UserResponseDto> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> UserResponseDto.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .status(user.getActive() ? "ACTIVE" : "INACTIVE")
                        .build())
                .toList();
    }

    public UserResponseDto getUserById(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getActive() ? "ACTIVE" : "INACTIVE")
                .build();
    }

    public void deleteUser(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setActive(false); 
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}