package com.utility.payment.service;

import com.utility.payment.exception.ApiException;
import com.utility.payment.model.Invoice;
import com.utility.payment.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoicePdfServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoicePdfService invoicePdfService;

    InvoicePdfServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateInvoicePdf_success() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-1");
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setBillingMonth(1);
        invoice.setBillingYear(2025);
        invoice.setBillId("B1");
        invoice.setPaymentId("P1");
        invoice.setConsumerId("C1");
        invoice.setEnergyCharge(100);
        invoice.setTax(10);
        invoice.setPenalty(0);
        invoice.setTotalAmount(110);

        when(invoiceRepository.findByPaymentId("P1"))
                .thenReturn(Optional.of(invoice));

        byte[] pdf = invoicePdfService.generateInvoicePdf("P1");

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    void generateInvoicePdf_invoiceNotFound() {
        when(invoiceRepository.findByPaymentId("P1"))
                .thenReturn(Optional.empty());

        ApiException ex = assertThrows(
                ApiException.class,
                () -> invoicePdfService.generateInvoicePdf("P1")
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
}
