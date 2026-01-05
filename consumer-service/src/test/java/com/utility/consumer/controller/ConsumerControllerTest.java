package com.utility.consumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.consumer.dto.request.ConsumerRequestDTO;
import com.utility.consumer.dto.response.ConsumerResponseDTO;
import com.utility.consumer.service.ConsumerService;

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

@WebMvcTest(ConsumerController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConsumerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsumerService consumerService;

    @Autowired
    private ObjectMapper objectMapper;
    private ConsumerResponseDTO mockResponse() {
        return new ConsumerResponseDTO(
                "C1",
                "Ananya",
                "a@gmail.com",
                "9999999999",
                "BLR",
                true
        );
    }

    @Test
    void createConsumer_success() throws Exception {

        ConsumerRequestDTO dto = new ConsumerRequestDTO();
        dto.setFullName("Ananya");
        dto.setEmail("a@gmail.com");
        dto.setMobileNumber("9999999999");
        dto.setAddress("BLR");

        Mockito.when(consumerService.createConsumer(Mockito.any()))
                .thenReturn(mockResponse());

        mockMvc.perform(post("/consumers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getConsumer_success() throws Exception {

    	Mockito.when(consumerService.getConsumer("C1"))
        .thenReturn(mockResponse());

        mockMvc.perform(get("/consumers/C1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllConsumers_success() throws Exception {

    	Mockito.when(consumerService.getAllConsumers())
        .thenReturn(List.of(mockResponse()));

        mockMvc.perform(get("/consumers"))
                .andExpect(status().isOk());
    }

    @Test
    void updateConsumer_success() throws Exception {

        ConsumerRequestDTO dto = new ConsumerRequestDTO();
        dto.setFullName("Updated");
        dto.setAddress("Bangalore"); // ✅ REQUIRED FIELD

        Mockito.when(consumerService.updateConsumer(Mockito.eq("C1"), Mockito.any()))
                .thenReturn(mockResponse());

        mockMvc.perform(put("/consumers/C1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deactivateConsumer_success() throws Exception {

        mockMvc.perform(delete("/consumers/C1"))
                .andExpect(status().isNoContent());
    }
}