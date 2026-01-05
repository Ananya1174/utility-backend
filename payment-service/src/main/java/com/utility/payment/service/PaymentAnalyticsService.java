package com.utility.payment.service;

import com.utility.payment.dto.dashboard.*;
import com.utility.payment.feign.BillingClient;
import com.utility.payment.model.Payment;
import com.utility.payment.model.PaymentMode;
import com.utility.payment.model.PaymentStatus;
import com.utility.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentAnalyticsService {

    private final PaymentRepository paymentRepository;
    private final BillingClient billingClient;

    /* ================= DASHBOARD ================= */

    public RevenueSummaryDto getMonthlyRevenue(int month, int year) {

        List<Payment> payments =
                paymentRepository.findByBillingMonthAndBillingYear(month, year)
                        .stream()
                        .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                        .toList();

        double revenue = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        return new RevenueSummaryDto(
                month,
                year,
                revenue,
                payments.size()
        );
    }

    public OutstandingSummaryDto getOutstandingSummary() {

        double totalPaid = paymentRepository
                .findByStatus(PaymentStatus.SUCCESS)
                .stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        double totalBilled = billingClient.getTotalBilled();

        double outstanding = Math.max(totalBilled - totalPaid, 0);

        // Billing service owns total billed
        return new OutstandingSummaryDto(
        		totalBilled,
                totalPaid,
                outstanding
        );
    }

    /* ================= PAYMENTS SUMMARY ================= */

    public PaymentsSummaryDto getPaymentsSummary(int month, int year) {

        List<Payment> payments =
                paymentRepository.findByBillingMonthAndBillingYear(month, year);

        long success = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .count();

        long failed = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.FAILED)
                .count();

        return new PaymentsSummaryDto(
                month,
                year,
                success,
                failed
        );
    }

    public FailedPaymentSummaryDto getFailedPaymentsSummary(int month, int year) {

        List<Payment> failed =
                paymentRepository.findByBillingMonthAndBillingYear(month, year)
                        .stream()
                        .filter(p -> p.getStatus() == PaymentStatus.FAILED)
                        .toList();

        double amount = failed.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        return new FailedPaymentSummaryDto(
                failed.size(),
                amount
        );
    }
    public List<MonthlyOutstandingDto> getMonthlyOutstanding(int year) {

        List<MonthlyOutstandingDto> result = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {

            // 🔹 Total Paid (SUCCESS payments only)
            double totalPaid =
                    paymentRepository
                            .findByBillingMonthAndBillingYearAndStatus(
                                    month,
                                    year,
                                    PaymentStatus.SUCCESS
                            )
                            .stream()
                            .mapToDouble(Payment::getAmount)
                            .sum();

            // 🔹 Total Billed (from Billing Service)
            double totalBilled =
                    billingClient.getTotalBilledForMonth(month, year);

            double outstanding =
                    Math.max(0, totalBilled - totalPaid);

            result.add(
                    new MonthlyOutstandingDto(
                            month,
                            Month.of(month).name(),
                            totalBilled,
                            totalPaid,
                            outstanding
                    )
            );
        }

        return result;
    }

    /* ================= REPORTS ================= */

    public List<PaymentModeSummaryDto> getRevenueByMode(int month, int year) {

        return paymentRepository
                .findByBillingMonthAndBillingYear(month, year)
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .collect(Collectors.groupingBy(
                        Payment::getMode,
                        Collectors.summingDouble(Payment::getAmount)
                ))
                .entrySet()
                .stream()
                .map(e -> new PaymentModeSummaryDto(e.getKey(), e.getValue()))
                .toList();
    }

    public List<ConsumerPaymentSummaryDto> getConsumerPaymentSummary(
            int month,
            int year) {

        return paymentRepository
                .findByBillingMonthAndBillingYear(month, year)
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .collect(Collectors.groupingBy(Payment::getConsumerId))
                .entrySet()
                .stream()
                .map(e -> new ConsumerPaymentSummaryDto(
                        e.getKey(),
                        e.getValue().size(),
                        e.getValue()
                                .stream()
                                .mapToDouble(Payment::getAmount)
                                .sum()
                ))
                .toList();
    }

    public List<RevenueSummaryDto> getYearlyRevenue(int year) {

        return paymentRepository.findByBillingYear(year)
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .collect(Collectors.groupingBy(
                        Payment::getBillingMonth,
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(e -> {
                    double revenue = e.getValue()
                            .stream()
                            .mapToDouble(Payment::getAmount)
                            .sum();

                    return new RevenueSummaryDto(
                            e.getKey(),
                            year,
                            revenue,
                            e.getValue().size()
                    );
                })
                .toList();
    }
}