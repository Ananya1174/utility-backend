package com.utility.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.auth.dto.request.AccountRequestDto;
import com.utility.auth.dto.request.AccountRequestReviewDto;
import com.utility.auth.model.AccountRequest;
import com.utility.auth.model.AccountRequestStatus;
import com.utility.auth.service.AccountRequestService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountRequestService accountRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    // ================= CREATE REQUEST =================
    @Test
    void createAccountRequest_success() throws Exception {

        AccountRequestDto dto = new AccountRequestDto();
        dto.setName("Ananya");
        dto.setEmail("a@gmail.com");
        dto.setPhone("9999999999");
        dto.setAddress("BLR");

        AccountRequest request = new AccountRequest();
        request.setRequestId("R1");
        request.setName("Ananya");
        request.setEmail("a@gmail.com");
        request.setPhone("9999999999");
        request.setAddress("BLR");
        request.setCreatedAt(LocalDateTime.now());

        Mockito.when(accountRequestService.createAccountRequest(Mockito.any()))
                .thenReturn(request);

        mockMvc.perform(post("/auth/account-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requestId").value("R1"))
                .andExpect(jsonPath("$.name").value("Ananya"));
    }

    // ================= GET PENDING =================
    @Test
    @WithMockUser(roles = "ADMIN")
    void getPendingRequests_success() throws Exception {

        Mockito.when(accountRequestService.getPendingRequests())
                .thenReturn(List.of());

        mockMvc.perform(get("/auth/account-requests/pending"))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void getPendingRequests_withData() throws Exception {

        AccountRequest req = AccountRequest.builder()
                .requestId("R1")
                .name("Ananya")
                .email("a@gmail.com")
                .phone("999")
                .address("BLR")
                .status(AccountRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(accountRequestService.getPendingRequests())
                .thenReturn(List.of(req));

        mockMvc.perform(get("/auth/account-requests/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value("R1"));
    }

    // ================= REVIEW REQUEST =================
    @Test
    @WithMockUser(roles = "ADMIN")
    void reviewRequest_success() throws Exception {

        AccountRequestReviewDto dto = new AccountRequestReviewDto();
        dto.setRequestId("R1");
        dto.setDecision("APPROVE"); // 👈 STRING, not boolean

        mockMvc.perform(post("/auth/account-requests/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("Account request reviewed successfully"));
    }
}