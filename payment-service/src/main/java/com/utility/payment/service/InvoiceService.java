package com.utility.payment.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.utility.payment.exception.ApiException;
import com.utility.payment.model.Invoice;
import com.utility.payment.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public Invoice getInvoiceByPaymentId(String paymentId) {
        return invoiceRepository.findByPaymentId(paymentId)
                .orElseThrow(() ->
                        new ApiException("Invoice not found", HttpStatus.NOT_FOUND));
    }

        public byte[] generateInvoicePdf(String paymentId) {
        throw new UnsupportedOperationException(
                "PDF generation not implemented yet");
    }
}