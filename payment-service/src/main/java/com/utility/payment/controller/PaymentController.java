package com.utility.payment.controller;

import com.utility.payment.dto.*;
import com.utility.payment.model.Invoice;
import com.utility.payment.service.InvoicePdfService;
import com.utility.payment.service.InvoiceService;
import com.utility.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;
	private final InvoiceService invoiceService;
	private final InvoicePdfService invoicePdfService;

	@PostMapping("/online/initiate")
	@ResponseStatus(HttpStatus.CREATED)
	public PaymentResponse initiateOnline(@Valid @RequestBody InitiateOnlinePaymentRequest request) {

		return paymentService.initiateOnline(request);
	}

	@PostMapping("/online/confirm")
	public ResponseEntity<Map<String, String>> confirmOnline(@Valid @RequestBody ConfirmOtpRequest request) {

		paymentService.confirmOtp(request);

		return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Payment confirmed successfully"));
	}

	@PostMapping("/offline")
	@ResponseStatus(HttpStatus.CREATED)
	public Map<String, String> offlinePayment(@Valid @RequestBody OfflinePaymentRequest request) {

		paymentService.offlinePayment(request);

		return Map.of("status", "SUCCESS", "message", "Offline payment recorded successfully");
	}

	@GetMapping("/bill/{billId}")
	public List<PaymentResponse> getPaymentsByBill(@PathVariable("billId") String billId) {

		return paymentService.getPaymentsByBill(billId);
	}

	@GetMapping("/consumer/{consumerId}")
	public List<PaymentResponse> getPaymentsByConsumer(@PathVariable("consumerId") String consumerId) {

		return paymentService.getPaymentsByConsumer(consumerId);
	}

	@GetMapping("/outstanding/{billId}")
	public OutstandingResponse outstanding(@PathVariable("billId") String billId) {

		return paymentService.getOutstanding(billId);
	}

	@GetMapping("/invoice/{paymentId}")
	public Invoice getInvoice(@PathVariable("paymentId") String paymentId) {

		return invoiceService.getInvoiceByPaymentId(paymentId);
	}

	@GetMapping("/invoice/{paymentId}/download")
	public ResponseEntity<byte[]> downloadInvoice(@PathVariable("paymentId") String paymentId) {

		byte[] pdf = invoicePdfService.generateInvoicePdf(paymentId);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + paymentId + ".pdf")
				.body(pdf);
	}

    @GetMapping
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }
}