package com.utility.payment.config;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.utility.payment.dto.BillResponse;
import com.utility.payment.feign.BillingClient;
import com.utility.payment.model.BillStatus;
import com.utility.payment.model.Invoice;
import com.utility.payment.model.Payment;
import com.utility.payment.model.PaymentMode;
import com.utility.payment.model.PaymentStatus;
import com.utility.payment.repository.InvoiceRepository;
import com.utility.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PaymentDataSeeder {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final BillingClient billingClient;

    @Bean
    CommandLineRunner seedPayments() {
        return args -> {

            if (paymentRepository.count() > 0) {
                return;
            }

            List<BillResponse> paidBills =
                    billingClient.getAllBills(
                            BillStatus.PAID,
                            null,
                            null,
                            null
                    );

            LocalDate today = LocalDate.now();

            for (BillResponse bill : paidBills) {

                LocalDate paymentDate = generatePaymentDate(bill.getDueDate());

                long ageInDays =
                        ChronoUnit.DAYS.between(paymentDate, today);

                boolean success = isPaymentSuccessful(ageInDays);

                Payment payment = buildPayment(bill, paymentDate, success);
                paymentRepository.save(payment);

                if (success) {
                    Invoice invoice = buildInvoice(bill, payment);
                    invoiceRepository.save(invoice);
                }
            }
        };
    }

    // ---------- Helper Methods ----------

    private LocalDate generatePaymentDate(LocalDate dueDate) {
        // SecureRandom instead of Random / Math.random
    	return dueDate.plusDays(RANDOM.nextInt(10) - 5L);
    }

    private boolean isPaymentSuccessful(long ageInDays) {
        if (ageInDays > 60) {
            return RANDOM.nextDouble() < 0.9;
        }
        return RANDOM.nextDouble() < 0.7;
    }

    private Payment buildPayment(
            BillResponse bill,
            LocalDate paymentDate,
            boolean success
    ) {
        Payment payment = new Payment();
        payment.setBillId(bill.getId());
        payment.setConsumerId(bill.getConsumerId());
        payment.setAmount(bill.getPayableAmount());
        payment.setMode(selectPaymentMode());
        payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setBillingMonth(bill.getBillingMonth());
        payment.setBillingYear(bill.getBillingYear());

        payment.setCreatedAt(
                paymentDate.atTime(10 + RANDOM.nextInt(6), 0)
        );

        payment.setConfirmedAt(
                success
                        ? payment.getCreatedAt().plusMinutes(2)
                        : null
        );

        return payment;
    }

    private PaymentMode selectPaymentMode() {
        return RANDOM.nextDouble() < 0.6
                ? PaymentMode.ONLINE
                : PaymentMode.OFFLINE;
    }

    private Invoice buildInvoice(BillResponse bill, Payment payment) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setBillId(bill.getId());
        invoice.setPaymentId(payment.getId());
        invoice.setConsumerId(bill.getConsumerId());
        invoice.setBillingMonth(bill.getBillingMonth());
        invoice.setBillingYear(bill.getBillingYear());
        invoice.setEnergyCharge(bill.getEnergyCharge());
        invoice.setTax(bill.getTax());
        invoice.setPenalty(bill.getPenalty());
        invoice.setAmountPaid(payment.getAmount());
        invoice.setTotalAmount(payment.getAmount());
        invoice.setInvoiceDate(payment.getConfirmedAt());
        return invoice;
    }
}