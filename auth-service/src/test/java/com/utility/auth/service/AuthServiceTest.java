package com.utility.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.utility.auth.dto.response.LoginResponseDto;
import com.utility.auth.dto.response.UserResponseDto;
import com.utility.auth.event.NotificationPublisher;
import com.utility.auth.exception.ResourceNotFoundException;
import com.utility.auth.exception.UserAlreadyExistsException;
import com.utility.auth.model.PasswordResetToken;
import com.utility.auth.model.Role;
import com.utility.auth.model.User;
import com.utility.auth.repository.PasswordResetTokenRepository;
import com.utility.auth.repository.UserRepository;
import com.utility.auth.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock AuthenticationManager authenticationManager;
    @Mock JwtUtil jwtUtil;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock NotificationPublisher notificationPublisher;

    @InjectMocks AuthService authService;

    // ---------------- REGISTER ----------------

    @Test
    void registerUser_success() {
        User user = User.builder()
                .username("admin")
                .email("a@gmail.com")
                .password("raw")
                .build();

        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("a@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("raw")).thenReturn("enc");
        when(userRepository.save(any())).thenReturn(user);

        User saved = authService.registerUser(user);

        assertNotNull(saved);
    }

    @Test
    void registerUser_usernameExists() {
        User user = User.builder()
                .username("admin")
                .build();

        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.registerUser(user));
    }

    @Test
    void registerUser_emailExists() {
        User user = User.builder()
                .username("admin")
                .email("a@gmail.com")
                .build();

        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("a@gmail.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.registerUser(user));
    }

    // ---------------- LOGIN ----------------

    @Test
    void login_success() {
        User user = User.builder()
                .userId("U1")
                .username("admin")
                .role(Role.ADMIN)
                .passwordChangeRequired(false)
                .build();

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken("U1", "admin", "ADMIN"))
                .thenReturn("token");

        LoginResponseDto res = authService.login("admin", "pwd");

        assertEquals("token", res.getAccessToken());
    }

    @Test
    void login_userNotFound_lambdaCovered() {
        when(userRepository.findByUsername("x"))
                .thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.login("x", "p"));
    }

    // ---------------- FORGOT PASSWORD ----------------

    @Test
    void forgotPassword_existingUser_lambdaCovered() {
        User user = User.builder().email("a@gmail.com").build();

        PasswordResetToken oldToken = PasswordResetToken.builder()
                .used(false)
                .build();

        when(userRepository.findByEmail("a@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordResetTokenRepository.findByEmailAndUsedFalse("a@gmail.com"))
                .thenReturn(List.of(oldToken));

        authService.forgotPassword("a@gmail.com");

        assertTrue(oldToken.getUsed());
        verify(notificationPublisher).publishPasswordReset(any());
    }

    @Test
    void forgotPassword_userNotFound_lambdaCovered() {
        when(userRepository.findByEmail("x@gmail.com"))
                .thenReturn(Optional.empty());

        authService.forgotPassword("x@gmail.com");

        verifyNoInteractions(notificationPublisher);
    }

    // ---------------- RESET PASSWORD ----------------

    @Test
    void resetPassword_success() {
        PasswordResetToken token = PasswordResetToken.builder()
                .email("a@gmail.com")
                .used(false)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();

        User user = User.builder().email("a@gmail.com").build();

        when(passwordResetTokenRepository.findByToken("t"))
                .thenReturn(Optional.of(token));
        when(userRepository.findByEmail("a@gmail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any()))
                .thenReturn("enc");

        authService.resetPassword("t", "ValidPass@123");

        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).save(token);
    }

    @Test
    void resetPassword_usedToken_lambdaCovered() {
        PasswordResetToken token = PasswordResetToken.builder()
                .used(true)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();

        when(passwordResetTokenRepository.findByToken("t"))
                .thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class,
                () -> authService.resetPassword("t", "ValidPass@123"));
    }

    @Test
    void resetPassword_userNotFound_lambdaCovered() {
        PasswordResetToken token = PasswordResetToken.builder()
                .email("a@gmail.com")
                .used(false)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();

        when(passwordResetTokenRepository.findByToken("t"))
                .thenReturn(Optional.of(token));
        when(userRepository.findByEmail("a@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authService.resetPassword("t", "ValidPass@123"));
    }

    // ---------------- CHANGE PASSWORD ----------------

    @Test
    void changePassword_success() {
        User user = User.builder()
                .userId("U1")
                .password("old")
                .build();

        when(userRepository.findById("U1"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "old"))
                .thenReturn(true);
        when(passwordEncoder.matches("ValidPass@123", "old"))
                .thenReturn(false);
        when(passwordEncoder.encode("ValidPass@123"))
                .thenReturn("encoded");

        authService.changePassword("U1", "old", "ValidPass@123");

        verify(userRepository).save(user);
    }

    @Test
    void changePassword_wrongOldPassword_lambdaCovered() {
        User user = User.builder().password("old").build();

        when(userRepository.findById("U1"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("x", "old"))
                .thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> authService.changePassword("U1", "x", "new"));
    }

    @Test
    void changePassword_userNotFound_lambdaCovered() {
        when(userRepository.findById("U1"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authService.changePassword("U1", "o", "n"));
    }

    // ---------------- GET USER ----------------

    @Test
    void getUserById_success() {
        User user = User.builder()
                .userId("U1")
                .username("admin")
                .email("a@gmail.com")
                .role(Role.ADMIN)
                .active(true)
                .build();

        when(userRepository.findById("U1"))
                .thenReturn(Optional.of(user));

        UserResponseDto dto = authService.getUserById("U1");

        assertEquals("ACTIVE", dto.getStatus());
    }

    @Test
    void getUserById_userNotFound_lambdaCovered() {
        when(userRepository.findById("U1"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authService.getUserById("U1"));
    }

    // ---------------- GET ALL USERS ----------------

    @Test
    void getAllUsers_streamLambdaCovered() {
        User user = User.builder()
                .userId("U1")
                .username("admin")
                .email("a@gmail.com")
                .role(Role.ADMIN)
                .active(false)
                .build();

        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserResponseDto> users = authService.getAllUsers();

        assertEquals("INACTIVE", users.get(0).getStatus());
    }

    // ---------------- DELETE USER ----------------

    @Test
    void deleteUser_success() {
        User user = User.builder().active(true).build();

        when(userRepository.findById("U1"))
                .thenReturn(Optional.of(user));

        authService.deleteUser("U1");

        assertFalse(user.getActive());
    }

    @Test
    void deleteUser_userNotFound_lambdaCovered() {
        when(userRepository.findById("U1"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> authService.deleteUser("U1"));
    }
}