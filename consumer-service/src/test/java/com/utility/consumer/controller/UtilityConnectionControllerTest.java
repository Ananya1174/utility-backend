package com.utility.consumer.controller;

import com.utility.consumer.dto.response.ConnectionResponseDto;
import com.utility.consumer.service.UtilityConnectionService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UtilityConnectionController.class)
@AutoConfigureMockMvc(addFilters = false)
class UtilityConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UtilityConnectionService connectionService;

    @Test
    void getMyConnections_success() throws Exception {

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken("C1", null)
        );
        SecurityContextHolder.setContext(context);

        Mockito.when(connectionService.getConnectionsByUserId("C1"))
                .thenReturn(List.of(new ConnectionResponseDto()));

        mockMvc.perform(get("/connections"))
                .andExpect(status().isOk());
    }

    @Test
    void getConnectionById_success() throws Exception {

        Mockito.when(connectionService.getConnectionById("ID1"))
                .thenReturn(new ConnectionResponseDto());

        mockMvc.perform(get("/connections/ID1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllConnections_internal_success() throws Exception {

        Mockito.when(connectionService.getAllConnections())
                .thenReturn(List.of(new ConnectionResponseDto()));

        mockMvc.perform(get("/connections/internal/all"))
                .andExpect(status().isOk());
    }
}