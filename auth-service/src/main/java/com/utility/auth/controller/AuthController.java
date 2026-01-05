package com.utility.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utility.auth.dto.request.ChangePasswordRequestDto;
import com.utility.auth.dto.request.ForgotPasswordRequestDto;
import com.utility.auth.dto.request.LoginRequestDto;
import com.utility.auth.dto.request.RegisterRequestDto;
import com.utility.auth.dto.request.ResetPasswordRequestDto;
import com.utility.auth.dto.response.LoginResponseDto;
import com.utility.auth.dto.response.RegisterResponseDto;
import com.utility.auth.dto.response.UserResponseDto;
import com.utility.auth.model.Role;
import com.utility.auth.model.User;
import com.utility.auth.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> registerUser(
            @Valid @RequestBody RegisterRequestDto request) {

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.valueOf(request.getRole()))
                .build();

        User savedUser = authService.registerUser(user);

        RegisterResponseDto response =
                new RegisterResponseDto(savedUser.getUserId(), "User registered successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {

        LoginResponseDto response =
                authService.login(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(response);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto request) {

        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Password reset link sent to registered email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto request) {

        authService.resetPassword(
                request.getResetToken(),
                request.getNewPassword());

        return ResponseEntity.ok("Password reset successful");
    }
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Unauthorized");
        }

        String userId = authentication.getPrincipal().toString(); // 👈 userId from JWT

        authService.changePassword(
                userId,
                request.getOldPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok("Password updated successfully");
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String userId) {

        UserResponseDto user = authService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") String userId) {
        System.out.println("Deleting user with ID: " + userId);
        authService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}