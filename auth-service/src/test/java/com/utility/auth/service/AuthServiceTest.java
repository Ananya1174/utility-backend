package com.utility.auth.service;

import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private AuthService authService;

    // ---------------- REGISTER ----------------
    @Test
    void registerUser_success() {

        User user = User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password("raw")
                .build();

        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("admin@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("raw")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(user);

        User saved = authService.registerUser(user);

        assertNotNull(saved);
        verify(userRepository).save(user);
    }

    // ---------------- LOGIN ----------------
    @Test
    void login_success() {

        User user = User.builder()
                .userId("U1")
                .username("admin")
                .role(Role.ADMIN)
                .password("encoded")
                .passwordChangeRequired(false)
                .build();

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken("U1", "admin", "ADMIN"))
                .thenReturn("jwt-token");

        LoginResponseDto response =
                authService.login("admin", "password");

        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("ADMIN", response.getRole());
    }
    @Test
    void login_success_executesLambda() {
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

        LoginResponseDto res = authService.login("admin", "pass");

        assertNotNull(res.getAccessToken());
    }

    // ---------------- FORGOT PASSWORD ----------------
    @Test
    void forgotPassword_existingUser() {

        User user = User.builder()
                .email("test@gmail.com")
                .build();

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordResetTokenRepository.findByEmailAndUsedFalse("test@gmail.com"))
                .thenReturn(List.of());

        authService.forgotPassword("test@gmail.com");

        verify(notificationPublisher)
                .publishPasswordReset(any());
    }

    // ---------------- RESET PASSWORD ----------------
    @Test
    void resetPassword_success() {

        PasswordResetToken token = PasswordResetToken.builder()
                .email("test@gmail.com")
                .token("token123")
                .used(false)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();

        User user = User.builder()
                .email("test@gmail.com")
                .password("old")
                .build();

        when(passwordResetTokenRepository.findByToken("token123"))
                .thenReturn(Optional.of(token));

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("NewPassword@123"))
                .thenReturn("encoded");

        authService.resetPassword("token123", "NewPassword@123");

        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).save(token);
    }

    // ---------------- CHANGE PASSWORD ----------------
    @Test
    void changePassword_success() {

        User user = User.builder()
                .userId("U1")
                .password("oldEncoded")
                .build();

        when(userRepository.findById("U1"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("old", "oldEncoded"))
                .thenReturn(true);

        when(passwordEncoder.matches("NewPassword@123", "oldEncoded"))
                .thenReturn(false);

        when(passwordEncoder.encode("NewPassword@123"))
                .thenReturn("newEncoded");

        authService.changePassword("U1", "old", "NewPassword@123");

        verify(userRepository).save(user);
    }
    @Test
    void registerUser_usernameExists() {
        User user = User.builder().username("admin").build();
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.registerUser(user));
    }
    @Test
    void resetPassword_tokenExpired() {
        PasswordResetToken token = PasswordResetToken.builder()
                .token("t")
                .expiryDate(LocalDateTime.now().minusMinutes(1))
                .used(false)
                .build();

        when(passwordResetTokenRepository.findByToken("t"))
                .thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class,
                () -> authService.resetPassword("t", "NewPassword@123"));
    }
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

        assertEquals("U1", dto.getUserId());
    }
    @Test
    void changePassword_wrongOldPassword() {
        User user = User.builder().password("encoded").build();

        when(userRepository.findById("U1"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("old", "encoded"))
                .thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> authService.changePassword("U1", "old", "NewPassword@123"));
    }

    // ---------------- GET USERS ----------------
    @Test
    void getAllUsers_success() {

        User user = User.builder()
                .userId("U1")
                .username("admin")
                .email("a@gmail.com")
                .role(Role.ADMIN)
                .active(true)
                .build();

        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserResponseDto> users =
                authService.getAllUsers();

        assertEquals(1, users.size());
    }

    // ---------------- DELETE USER ----------------
    @Test
    void deleteUser_success() {

        User user = User.builder()
                .userId("U1")
                .active(true)
                .build();

        when(userRepository.findById("U1"))
                .thenReturn(Optional.of(user));

        authService.deleteUser("U1");

        assertFalse(user.getActive());
        verify(userRepository).save(user);
    }
}