package com.utility.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utility.payment.dto.*;
import com.utility.payment.model.Invoice;
import com.utility.payment.model.PaymentMode;
import com.utility.payment.model.PaymentStatus;
import com.utility.payment.service.InvoicePdfService;
import com.utility.payment.service.InvoiceService;
import com.utility.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private InvoicePdfService invoicePdfService;

    @Test
    void initiateOnline_success() throws Exception {

        InitiateOnlinePaymentRequest request =
                new InitiateOnlinePaymentRequest("BILL1", "C1");

        PaymentResponse response =
                new PaymentResponse(
                        "P1",
                        "BILL1",
                        "C1",
                        1,
                        2025,
                        500.0,
                        PaymentMode.ONLINE,
                        PaymentStatus.INITIATED,
                        "TXN123",
                        null,
                        LocalDateTime.now(),
                        null
                );

        Mockito.when(paymentService.initiateOnline(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/payments/online/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void confirmOnline_success() throws Exception {

        ConfirmOtpRequest request =
                new ConfirmOtpRequest("P1", "123456");

        mockMvc.perform(post("/payments/online/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void offlinePayment_success() throws Exception {

        OfflinePaymentRequest request =
                new OfflinePaymentRequest("BILL1", "C1", "Cash payment");

        mockMvc.perform(post("/payments/offline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void getPaymentsByBill_success() throws Exception {

        PaymentResponse response =
                new PaymentResponse(
                        "P1",
                        "BILL1",
                        "C1",
                        1,
                        2025,
                        500.0,
                        PaymentMode.ONLINE,
                        PaymentStatus.SUCCESS,
                        "TXN123",
                        "INV1",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        Mockito.when(paymentService.getPaymentsByBill("BILL1"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/payments/bill/BILL1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPaymentsByConsumer_success() throws Exception {

        PaymentResponse response =
                new PaymentResponse(
                        "P1",
                        "BILL1",
                        "C1",
                        1,
                        2025,
                        500.0,
                        PaymentMode.ONLINE,
                        PaymentStatus.SUCCESS,
                        "TXN123",
                        "INV1",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        Mockito.when(paymentService.getPaymentsByConsumer("C1"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/payments/consumer/C1"))
                .andExpect(status().isOk());
    }

    @Test
    void outstanding_success() throws Exception {

        OutstandingResponse response =
                new OutstandingResponse(
                        "BILL1",
                        1000.0,
                        600.0,
                        400.0
                );

        Mockito.when(paymentService.getOutstanding("BILL1"))
                .thenReturn(response);

        mockMvc.perform(get("/payments/outstanding/BILL1"))
                .andExpect(status().isOk());
    }

    @Test
    void getInvoice_success() throws Exception {

        Mockito.when(invoiceService.getInvoiceByPaymentId("P1"))
                .thenReturn(new Invoice());

        mockMvc.perform(get("/payments/invoice/P1"))
                .andExpect(status().isOk());
    }

    @Test
    void downloadInvoice_success() throws Exception {

        Mockito.when(invoicePdfService.generateInvoicePdf("P1"))
                .thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/payments/invoice/P1/download"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllPayments_success() throws Exception {

        PaymentResponse response =
                new PaymentResponse(
                        "P1",
                        "BILL1",
                        "C1",
                        1,
                        2025,
                        500.0,
                        PaymentMode.ONLINE,
                        PaymentStatus.SUCCESS,
                        "TXN123",
                        "INV1",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        Mockito.when(paymentService.getAllPayments())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk());
    }
}
