package com.utility.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.auth.dto.request.*;
import com.utility.auth.dto.response.LoginResponseDto;
import com.utility.auth.model.Role;
import com.utility.auth.model.User;
import com.utility.auth.service.AuthService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    // ================= REGISTER =================
    @Test
    @WithMockUser(roles = "ADMIN")
    void registerUser_success() throws Exception {

        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername("admin");
        request.setEmail("admin@gmail.com");
        request.setPassword("password123");
        request.setRole("ADMIN");

        User savedUser = User.builder()
                .userId("U1")
                .username("admin")
                .role(Role.ADMIN)
                .build();

        Mockito.when(authService.registerUser(Mockito.any()))
                .thenReturn(savedUser);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("User registered successfully"));
    }

    // ================= LOGIN =================
    @Test
    void login_success() throws Exception {

        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("admin");
        request.setPassword("password123");

        LoginResponseDto response =
                new LoginResponseDto(
                        "jwt-token",
                        "Bearer",
                        "ADMIN",
                        false
                );

        Mockito.when(authService.login("admin", "password123"))
                .thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    // ================= FORGOT PASSWORD =================
    @Test
    void forgotPassword_success() throws Exception {

        ForgotPasswordRequestDto request = new ForgotPasswordRequestDto();
        request.setEmail("test@gmail.com");

        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("Password reset link sent to registered email"));
    }

    // ================= RESET PASSWORD =================
    @Test
    void resetPassword_success() throws Exception {

        ResetPasswordRequestDto request = new ResetPasswordRequestDto();
        request.setResetToken("token123");
        request.setNewPassword("newStrongPassword123");

        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("Password reset successful"));
    }
    @Test
    void changePassword_unauthorized() throws Exception {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto();
        dto.setOldPassword("old");
        dto.setNewPassword("NewPassword@123");

        mockMvc.perform(put("/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }
    @Test
    void changePassword_unauthorized_whenAuthenticationIsNull() throws Exception {

        SecurityContextHolder.clearContext(); // 👈 forces authentication = null

        ChangePasswordRequestDto dto = new ChangePasswordRequestDto();
        dto.setOldPassword("old");
        dto.setNewPassword("NewPassword@123");

        mockMvc.perform(put("/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
    }
    @Test
    void registerUser_invalidRequest_returnsBadRequest() throws Exception {

        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setUsername("u"); // invalid (min 4)
        dto.setEmail("a@gmail.com");
        dto.setPassword("password123");
        dto.setRole("ADMIN");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ================= GET ALL USERS =================
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_success() throws Exception {

        Mockito.when(authService.getAllUsers())
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/auth/users"))
                .andExpect(status().isOk());
    }

    // ================= DELETE USER =================
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_success() throws Exception {

        mockMvc.perform(delete("/auth/users/U1"))
                .andExpect(status().isNoContent());
    }
}