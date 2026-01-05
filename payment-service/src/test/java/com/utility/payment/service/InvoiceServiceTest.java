package com.utility.payment.service;

import com.utility.payment.exception.ApiException;
import com.utility.payment.model.Invoice;
import com.utility.payment.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    InvoiceServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInvoiceByPaymentId_success() {
        Invoice invoice = new Invoice();

        when(invoiceRepository.findByPaymentId("P1"))
                .thenReturn(Optional.of(invoice));

        Invoice result = invoiceService.getInvoiceByPaymentId("P1");

        assertNotNull(result);
    }

    @Test
    void getInvoiceByPaymentId_notFound() {
        when(invoiceRepository.findByPaymentId("P1"))
                .thenReturn(Optional.empty());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> invoiceService.getInvoiceByPaymentId("P1")
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
}
