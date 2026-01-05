package com.utility.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.billing.config.SecurityConfig;
import com.utility.billing.dto.TariffPlanDto;
import com.utility.billing.service.TariffPlanService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = TariffPlanController.class,
    excludeFilters = @Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TariffPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TariffPlanService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_success() throws Exception {
        TariffPlanDto dto = new TariffPlanDto("DOMESTIC", List.of());

        Mockito.when(service.createTariffPlan(Mockito.any()))
                .thenReturn(dto);

        mockMvc.perform(post("/tariffs/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void deactivate_success() throws Exception {
        Mockito.when(service.deactivateTariffPlan("1"))
                .thenReturn(Map.of("status", "deactivated"));

        mockMvc.perform(put("/tariffs/plans/1/deactivate"))
                .andExpect(status().isOk());
    }

    @Test
    void getPlans_success() throws Exception {
        Mockito.when(service.getPlans(true))
                .thenReturn(List.of());

        mockMvc.perform(get("/tariffs/plans")
                        .param("active", "true"))
                .andExpect(status().isOk());
    }
}