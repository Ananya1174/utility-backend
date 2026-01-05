package com.utility.billing.controller;

import com.utility.billing.dto.TariffResponseDto;
import com.utility.billing.model.UtilityType;
import com.utility.billing.service.TariffQueryService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TariffQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
class TariffQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TariffQueryService service;

    @Test
    void getTariffsByUtility_success() throws Exception {

        Mockito.when(service.getTariffsByUtility(UtilityType.ELECTRICITY))
                .thenReturn(new TariffResponseDto());

        mockMvc.perform(get("/tariffs")
                .param("utilityType", "ELECTRICITY"))
                .andExpect(status().isOk());
    }
}