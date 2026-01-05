package com.utility.meter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.meter.dto.CreateMeterReadingRequest;
import com.utility.meter.dto.MeterReadingResponse;
import com.utility.meter.exception.ApiException;
import com.utility.meter.model.UtilityType;
import com.utility.meter.service.MeterReadingService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeterReadingController.class)
@AutoConfigureMockMvc(addFilters = false)
class MeterReadingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeterReadingService service;

    @Autowired
    private ObjectMapper objectMapper;

    private MeterReadingResponse mockResponse() {
        MeterReadingResponse r = new MeterReadingResponse();
        r.setId("R1");
        r.setConsumerId("C1");
        r.setConnectionId("CON1");
        r.setUtilityType(UtilityType.ELECTRICITY);
        r.setMeterNumber("MTR1");
        return r;
    }

    @Test
    void addReading_success() throws Exception {

        CreateMeterReadingRequest req = new CreateMeterReadingRequest();
        req.setConsumerId("C1");
        req.setConnectionId("CON1");
        req.setUtilityType(UtilityType.ELECTRICITY);
        req.setMeterNumber("MTR1");
        req.setCurrentReading(100);
        req.setReadingMonth(1);
        req.setReadingYear(2025);

        Mockito.when(service.addReading(Mockito.any()))
                .thenReturn(mockResponse());

        mockMvc.perform(post("/meter-readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void byConsumer_success() throws Exception {

        Mockito.when(service.getByConsumer("C1"))
                .thenReturn(List.of(mockResponse()));

        mockMvc.perform(get("/meter-readings/consumer/C1"))
                .andExpect(status().isOk());
    }

    @Test
    void byConsumer_notFound() throws Exception {

        Mockito.when(service.getByConsumer("C1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/meter-readings/consumer/C1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void byMonth_success() throws Exception {

        Mockito.when(service.getByMonth(1, 2025))
                .thenReturn(List.of(mockResponse()));

        mockMvc.perform(get("/meter-readings/month")
                .param("month", "1")
                .param("year", "2025"))
                .andExpect(status().isOk());
    }

    @Test
    void byMonth_notFound() throws Exception {

        Mockito.when(service.getByMonth(1, 2025))
                .thenReturn(List.of());

        mockMvc.perform(get("/meter-readings/month")
                .param("month", "1")
                .param("year", "2025"))
                .andExpect(status().isNotFound());
    }

    @Test
    void latest_success() throws Exception {

        Mockito.when(service.getLatest("CON1"))
                .thenReturn(mockResponse());

        mockMvc.perform(get("/meter-readings/latest/CON1"))
                .andExpect(status().isOk());
    }

    @Test
    void byConnection_success() throws Exception {

        Mockito.when(service.getByConnection("CON1"))
                .thenReturn(List.of(mockResponse()));

        mockMvc.perform(get("/meter-readings/connection/CON1"))
                .andExpect(status().isOk());
    }

    @Test
    void byConnection_notFound() throws Exception {

    	Mockito.when(service.getByConnection("CON1"))
        .thenThrow(new ApiException(
                "No meter readings found for this connection",
                HttpStatus.NOT_FOUND
        ));

        mockMvc.perform(get("/meter-readings/connection/CON1"))
                .andExpect(status().isNotFound());
    }
}