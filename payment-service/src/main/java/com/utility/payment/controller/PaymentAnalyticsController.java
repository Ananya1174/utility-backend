package com.utility.payment.controller;

import com.utility.payment.dto.dashboard.*;
import com.utility.payment.service.PaymentAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard/payments")
@RequiredArgsConstructor
public class PaymentAnalyticsController {

    private final PaymentAnalyticsService analyticsService;

    @GetMapping("/revenue-summary")
    public RevenueSummaryDto revenueSummary(
    		 @RequestParam(name = "month") int month,
             @RequestParam(name = "year") int year) {

        return analyticsService.getMonthlyRevenue(month, year);
    }

    @GetMapping("/outstanding-summary")
    public OutstandingSummaryDto outstandingSummary() {

        return analyticsService.getOutstandingSummary();
    }

    @GetMapping("/payments-summary")
    public PaymentsSummaryDto paymentsSummary(
    		 @RequestParam(name = "month") int month,
             @RequestParam(name = "year") int year) {

        return analyticsService.getPaymentsSummary(month, year);
    }

    @GetMapping("/failed-summary")
    public FailedPaymentSummaryDto failedPayments(
    		 @RequestParam(name = "month") int month,
             @RequestParam(name = "year") int year) {

        return analyticsService.getFailedPaymentsSummary(month, year);
    }
    @GetMapping("/outstanding-monthly")
    public List<MonthlyOutstandingDto> monthlyOutstanding(
    		@RequestParam(name = "year") int year
    ) {
        return analyticsService.getMonthlyOutstanding(year);
    }

    @GetMapping("/revenue-by-mode")
    public List<PaymentModeSummaryDto> revenueByMode(
    		 @RequestParam(name = "month") int month,
             @RequestParam(name = "year") int year) {

        return analyticsService.getRevenueByMode(month, year);
    }

    @GetMapping("/consumer-summary")
    public List<ConsumerPaymentSummaryDto> consumerPaymentSummary(
    		 @RequestParam(name = "month") int month,
             @RequestParam(name = "year") int year) {

        return analyticsService.getConsumerPaymentSummary(month, year);
    }

    @GetMapping("/revenue-yearly")
    public List<RevenueSummaryDto> yearlyRevenue(
    		@RequestParam(name = "year") int year) {

        return analyticsService.getYearlyRevenue(year);
    }
}