package com.utility.payment.config;

import com.utility.payment.feign.BillingClient;
import com.utility.payment.model.*;
import com.utility.payment.repository.InvoiceRepository;
import com.utility.payment.repository.PaymentRepository;
import com.utility.payment.dto.BillResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class PaymentDataSeeder {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final BillingClient billingClient;

    @Bean
    CommandLineRunner seedPayments() {
        return args -> {

            if (paymentRepository.count() > 0) {
                System.out.println("Payments already exist. Skipping seeding.");
                return;
            }

            System.out.println("🌱 Seeding Payments & Invoices...");

            // ✅ Only PAID bills should have payments
            List<BillResponse> paidBills =
                    billingClient.getAllBills(
                            BillStatus.PAID,
                            null,
                            null,
                            null
                    );

            Random random = new Random();
            LocalDate today = LocalDate.now();

            for (BillResponse bill : paidBills) {

                // 🔹 Payment date near bill due date
                LocalDate paymentDate =
                        bill.getDueDate().plusDays(random.nextInt(10) - 5);

                long ageInDays =
                        ChronoUnit.DAYS.between(paymentDate, today);

                // 🔹 Old bills → mostly SUCCESS
                boolean success;
                if (ageInDays > 60) {
                    success = random.nextDouble() < 0.9;
                } else {
                    success = random.nextDouble() < 0.7;
                }

                PaymentMode mode =
                        random.nextDouble() < 0.6
                                ? PaymentMode.ONLINE
                                : PaymentMode.OFFLINE;

                Payment payment = new Payment();
                payment.setBillId(bill.getId());
                payment.setConsumerId(bill.getConsumerId());
                payment.setAmount(bill.getPayableAmount());
                payment.setMode(mode);
                payment.setStatus(
                        success
                                ? PaymentStatus.SUCCESS
                                : PaymentStatus.FAILED
                );
                payment.setTransactionId(UUID.randomUUID().toString());
                payment.setBillingMonth(bill.getBillingMonth());
                payment.setBillingYear(bill.getBillingYear());

                payment.setCreatedAt(
                        paymentDate.atTime(10 + random.nextInt(6), 0)
                );
                payment.setConfirmedAt(
                        success
                                ? payment.getCreatedAt().plusMinutes(2)
                                : null
                );

                paymentRepository.save(payment);

                // ✅ Invoice ONLY if payment SUCCESS
                if (success) {
                    Invoice invoice = new Invoice();
                    invoice.setInvoiceNumber(
                            "INV-" + System.currentTimeMillis()
                    );
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
                    invoice.setInvoiceDate(
                            payment.getConfirmedAt()
                    );

                    invoiceRepository.save(invoice);
                }
            }

            System.out.println("✅ Payment & Invoice seeding completed");
        };
    }
}