package com.utility.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.billing.config.SecurityConfig;
import com.utility.billing.dto.TariffSlabDto;
import com.utility.billing.service.TariffSlabService;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = TariffSlabController.class,
    excludeFilters = @Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TariffSlabControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TariffSlabService slabService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTariffSlab_success() throws Exception {
        TariffSlabDto dto = new TariffSlabDto(0, 100, 5.5);

        Mockito.when(slabService.createSlab(Mockito.any()))
                .thenReturn(dto);

        mockMvc.perform(post("/tariffs/slabs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteTariffSlab_success() throws Exception {
        Mockito.when(slabService.deleteSlab("1"))
                .thenReturn(new TariffSlabDto(0, 100, 5.5));

        mockMvc.perform(delete("/tariffs/slabs/1"))
                .andExpect(status().isOk());
    }
}