package com.utility.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.billing.model.TariffPlan;
import com.utility.billing.service.TariffPlanService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TariffPlanController.class)
@AutoConfigureMockMvc(addFilters = false)
class TariffPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TariffPlanService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_success() throws Exception {

        Mockito.when(service.createTariffPlan(Mockito.any()))
                .thenReturn(new TariffPlan());

        mockMvc.perform(post("/tariffs/plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TariffPlan())))
                .andExpect(status().isCreated());
    }

    @Test
    void deactivate_success() throws Exception {

        Mockito.when(service.deactivateTariffPlan("T1"))
                .thenReturn(Map.of("status", "deactivated"));

        mockMvc.perform(put("/tariffs/plans/T1/deactivate"))
                .andExpect(status().isOk());
    }

    @Test
    void getPlans_success() throws Exception {

        Mockito.when(service.getPlans(true))
                .thenReturn(List.of(new TariffPlan()));

        mockMvc.perform(get("/tariffs/plans")
                .param("active", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void activePlans_success() throws Exception {

        Mockito.when(service.getActivePlans())
                .thenReturn(List.of(new TariffPlan()));

        mockMvc.perform(get("/tariffs/plans/active"))
                .andExpect(status().isOk());
    }
}