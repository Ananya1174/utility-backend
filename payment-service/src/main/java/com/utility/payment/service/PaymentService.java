package com.utility.payment.service;

import com.utility.common.dto.event.PaymentOtpEvent;
import com.utility.payment.model.BillStatus;
import com.utility.payment.config.RabbitMQConfig;
import com.utility.payment.dto.*;
import com.utility.payment.exception.ApiException;
import com.utility.payment.feign.BillingClient;
import com.utility.payment.feign.ConsumerClient;
import com.utility.payment.model.Invoice;
import com.utility.payment.model.Payment;
import com.utility.payment.model.PaymentMode;
import com.utility.payment.model.PaymentStatus;
import com.utility.payment.repository.InvoiceRepository;
import com.utility.payment.repository.PaymentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository repository;
	private final BillingClient billingClient;
	private final InvoiceRepository invoiceRepository;
	private final ConsumerClient consumerClient;
    private final RabbitTemplate rabbitTemplate; 

    public PaymentResponse initiateOnline(InitiateOnlinePaymentRequest request) {

        BillResponse bill = billingClient.getBill(request.billId());

        if (bill.getStatus() == BillStatus.PAID) {
            throw new ApiException("Bill already paid", HttpStatus.BAD_REQUEST);
        }

        // 1️⃣ Check outstanding
        double totalPaid = repository.findByBillId(request.billId()).stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .mapToDouble(Payment::getAmount)
                .sum();

        double outstanding = bill.getTotalAmount() - totalPaid;

        if (outstanding <= 0) {
            throw new ApiException("No outstanding amount", HttpStatus.BAD_REQUEST);
        }

        // 2️⃣ Check existing INITIATED payment
        Payment payment = repository
                .findByBillIdAndStatus(request.billId(), PaymentStatus.INITIATED)
                .orElseGet(() -> {
                    Payment p = new Payment();
                    p.setBillId(request.billId());
                    p.setConsumerId(request.consumerId());
                    p.setAmount(outstanding);
                    p.setMode(PaymentMode.ONLINE);
                    p.setStatus(PaymentStatus.INITIATED);
                    p.setTransactionId(UUID.randomUUID().toString());
                    p.setBillingMonth(bill.getBillingMonth());
                    p.setBillingYear(bill.getBillingYear());
                    p.setCreatedAt(LocalDateTime.now());
                    return p;
                });

        // 3️⃣ ALWAYS generate NEW OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        payment.setOtp(otp);
        payment.setOtpExpiry(Instant.now().plusSeconds(300));

        repository.save(payment);

        // 4️⃣ Fetch consumer email
        ConsumerResponse consumer = consumerClient.getConsumerById(payment.getConsumerId());
        String email = consumer.getEmail();

        // 5️⃣ Send OTP event
        PaymentOtpEvent event = new PaymentOtpEvent();
        event.setEmail(email);
        event.setOtp(otp);
        event.setValidMinutes(5);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.PAYMENT_OTP_KEY,
                event
        );

        System.out.println("📤 NEW OTP SENT TO " + email + " | OTP = " + otp);

        return PaymentResponse.from(payment, bill, null);
    }
    public List<PaymentResponse> getAllPayments() {

        List<Payment> payments = repository.findAllByOrderByCreatedAtDesc();

        if (payments.isEmpty()) {
            throw new ApiException("No payments found", HttpStatus.NOT_FOUND);
        }

        return payments.stream().map(payment -> {
            BillResponse bill = billingClient.getBill(payment.getBillId());
            Invoice invoice = invoiceRepository
                    .findByPaymentId(payment.getId())
                    .orElse(null);

            return PaymentResponse.from(
                    payment,
                    bill,
                    invoice != null ? invoice.getId() : null
            );
        }).toList();
    }
	public PaymentResponse confirmOtp(ConfirmOtpRequest request) {

		Payment payment = repository.findById(request.paymentId())
				.orElseThrow(() -> new ApiException("Payment not found", HttpStatus.NOT_FOUND));

		if (payment.getStatus() == PaymentStatus.SUCCESS) {
			throw new ApiException("Payment already completed", HttpStatus.BAD_REQUEST);
		}

		if (payment.getOtpExpiry().isBefore(Instant.now())) {
			payment.setStatus(PaymentStatus.FAILED);
			repository.save(payment);
			throw new ApiException("OTP expired", HttpStatus.BAD_REQUEST);
		}

		if (!payment.getOtp().equals(request.otp())) {
			throw new ApiException("Invalid OTP", HttpStatus.BAD_REQUEST);
		}

		payment.setStatus(PaymentStatus.SUCCESS);
		payment.setConfirmedAt(LocalDateTime.now());
		payment.setOtp(null);
		payment.setOtpExpiry(null);

		repository.save(payment);

		billingClient.markPaid(payment.getBillId());

		Invoice invoice = generateInvoice(payment);
		BillResponse bill = billingClient.getBill(payment.getBillId());

		return PaymentResponse.from(payment, bill, invoice.getId());
	}

	public PaymentResponse offlinePayment(OfflinePaymentRequest request) {

		BillResponse bill = billingClient.getBill(request.billId());

		if (bill.getStatus() == BillStatus.PAID)  {
			throw new ApiException("Bill already paid", HttpStatus.BAD_REQUEST);
		}

		double totalPaid = repository.findByBillId(request.billId()).stream()
				.filter(p -> p.getStatus() == PaymentStatus.SUCCESS).mapToDouble(Payment::getAmount).sum();

		double outstanding = bill.getTotalAmount() - totalPaid;

		if (outstanding <= 0) {
			throw new ApiException("No outstanding amount", HttpStatus.BAD_REQUEST);
		}

		billingClient.markPaid(request.billId());

		Payment payment = new Payment();
		payment.setBillId(request.billId());
		payment.setConsumerId(request.consumerId());
		payment.setAmount(outstanding);
		payment.setMode(PaymentMode.OFFLINE);
		payment.setStatus(PaymentStatus.SUCCESS);
		payment.setRemarks(request.remarks());
		payment.setTransactionId(UUID.randomUUID().toString());
		payment.setBillingMonth(bill.getBillingMonth());
		payment.setBillingYear(bill.getBillingYear());
		payment.setCreatedAt(LocalDateTime.now());
		payment.setConfirmedAt(LocalDateTime.now());

		repository.save(payment);

		Invoice invoice = generateInvoice(payment);

		return PaymentResponse.from(payment, bill, invoice.getId());
	}

	public OutstandingResponse getOutstanding(String billId) {

		BillResponse bill = billingClient.getBill(billId);

		double totalPaid = repository.findByBillId(billId).stream().filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
				.mapToDouble(Payment::getAmount).sum();

		return new OutstandingResponse(billId, bill.getTotalAmount(), totalPaid,
				Math.max(bill.getTotalAmount() - totalPaid, 0));
	}

	public List<PaymentResponse> getPaymentsByConsumer(String consumerId) {

		List<Payment> payments = repository.findByConsumerIdOrderByCreatedAtDesc(consumerId);

		if (payments.isEmpty()) {
			throw new ApiException("No payments found for consumerId: " + consumerId, HttpStatus.NOT_FOUND);
		}

		return payments.stream().map(p -> {
			BillResponse bill = billingClient.getBill(p.getBillId());
			Invoice invoice = invoiceRepository.findByPaymentId(p.getId()).orElse(null);

			return PaymentResponse.from(p, bill, invoice != null ? invoice.getId() : null);
		}).toList();
	}

	public List<PaymentResponse> getPaymentsByBill(String billId) {

		List<Payment> payments = repository.findByBillId(billId);

		if (payments.isEmpty()) {
			throw new ApiException("No payments found for billId: " + billId, HttpStatus.NOT_FOUND);
		}

		BillResponse bill = billingClient.getBill(billId);

		return payments.stream().map(p -> {
			Invoice invoice = invoiceRepository.findByPaymentId(p.getId()).orElse(null);

			return PaymentResponse.from(p, bill, invoice != null ? invoice.getId() : null);
		}).toList();
	}

	private Invoice generateInvoice(Payment payment) {
		BillResponse bill = billingClient.getBill(payment.getBillId());

		Invoice invoice = new Invoice();
		invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
		invoice.setBillId(payment.getBillId());
		invoice.setPaymentId(payment.getId());
		invoice.setConsumerId(payment.getConsumerId());
		invoice.setBillingMonth(bill.getBillingMonth());
		invoice.setBillingYear(bill.getBillingYear());
		invoice.setEnergyCharge(bill.getEnergyCharge());

		invoice.setAmountPaid(payment.getAmount());
		invoice.setTax(bill.getTax());
		invoice.setPenalty(bill.getPenalty());
		invoice.setTotalAmount(payment.getAmount());
		invoice.setInvoiceDate(LocalDateTime.now());

		return invoiceRepository.save(invoice);
	}
}