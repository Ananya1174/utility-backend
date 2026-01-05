package com.utility.consumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.consumer.dto.request.ApproveConnectionRequestDto;
import com.utility.consumer.dto.request.CreateConnectionRequestDto;
import com.utility.consumer.enums.UtilityType;
import com.utility.consumer.model.ConnectionRequest;
import com.utility.consumer.service.ConnectionRequestService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConnectionRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConnectionRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConnectionRequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createRequest_success() throws Exception {

        CreateConnectionRequestDto dto = new CreateConnectionRequestDto();
        dto.setUtilityType(UtilityType.ELECTRICITY);
        dto.setTariffPlan("DOMESTIC");

        mockMvc.perform(post("/connections/requests")
                .header("X-Consumer-Id", "C1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getPending_success() throws Exception {

        Mockito.when(requestService.getPendingRequests())
                .thenReturn(List.of(new ConnectionRequest()));

        mockMvc.perform(get("/connections/requests/pending"))
                .andExpect(status().isOk());
    }

    @Test
    void approve_success() throws Exception {

        ApproveConnectionRequestDto dto = new ApproveConnectionRequestDto();
        dto.setMeterNumber("MTR-1");

        mockMvc.perform(put("/connections/requests/R1/approve")
                .principal(() -> "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void reject_success() throws Exception {

        mockMvc.perform(put("/connections/requests/R1/reject")
                .principal(() -> "ADMIN"))
                .andExpect(status().isOk());
    }
}