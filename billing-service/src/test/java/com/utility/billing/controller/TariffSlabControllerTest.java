package com.utility.billing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.billing.model.TariffSlab;
import com.utility.billing.repository.TariffSlabRepository;
import com.utility.billing.service.TariffSlabService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TariffSlabController.class)
@AutoConfigureMockMvc(addFilters = false)
class TariffSlabControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TariffSlabService slabService;
    
    @MockBean
    private TariffSlabRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTariffSlab_success() throws Exception {

        TariffSlab slab = new TariffSlab();
        slab.setId("S1");

        mockMvc.perform(post("/tariffs/slabs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(slab)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteTariffSlab_success() throws Exception {

        TariffSlab slab = new TariffSlab();
        slab.setId("S1");

        when(slabService.deleteSlab("S1"))
                .thenReturn(slab);

        mockMvc.perform(delete("/tariffs/slabs/S1"))
                .andExpect(status().isOk());
    }
}