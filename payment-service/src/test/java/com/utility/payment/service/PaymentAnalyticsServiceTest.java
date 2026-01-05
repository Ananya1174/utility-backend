package com.utility.payment.service;

import com.utility.payment.dto.dashboard.*;
import com.utility.payment.feign.BillingClient;
import com.utility.payment.model.Payment;
import com.utility.payment.model.PaymentMode;
import com.utility.payment.model.PaymentStatus;
import com.utility.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentAnalyticsServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BillingClient billingClient;

    @InjectMocks
    private PaymentAnalyticsService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /* ================= MONTHLY REVENUE ================= */

    @Test
    void getMonthlyRevenue_success() {

        Payment p = new Payment();
        p.setAmount(500);
        p.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByBillingMonthAndBillingYear(1, 2025))
                .thenReturn(List.of(p));

        RevenueSummaryDto dto = service.getMonthlyRevenue(1, 2025);

        assertEquals(500, dto.totalRevenue());
        assertEquals(1, dto.successfulPayments());
    }

    /* ================= OUTSTANDING SUMMARY ================= */

    @Test
    void getOutstandingSummary_success() {

        Payment p = new Payment();
        p.setAmount(300);
        p.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByStatus(PaymentStatus.SUCCESS))
                .thenReturn(List.of(p));

        when(billingClient.getTotalBilled()).thenReturn(1000.0);

        OutstandingSummaryDto dto = service.getOutstandingSummary();

        assertEquals(1000.0, dto.totalBilled());
        assertEquals(300.0, dto.totalPaid());
        assertEquals(700.0, dto.outstandingAmount());
    }

    /* ================= PAYMENTS SUMMARY ================= */

    @Test
    void getPaymentsSummary_success() {

        Payment success = new Payment();
        success.setStatus(PaymentStatus.SUCCESS);

        Payment failed = new Payment();
        failed.setStatus(PaymentStatus.FAILED);

        when(paymentRepository.findByBillingMonthAndBillingYear(1, 2025))
                .thenReturn(List.of(success, failed));

        PaymentsSummaryDto dto = service.getPaymentsSummary(1, 2025);

        assertEquals(1, dto.successfulPayments());
        assertEquals(1, dto.failedPayments());
    }

    /* ================= FAILED PAYMENTS SUMMARY ================= */

    @Test
    void getFailedPaymentsSummary_success() {

        Payment failed = new Payment();
        failed.setStatus(PaymentStatus.FAILED);
        failed.setAmount(400);

        when(paymentRepository.findByBillingMonthAndBillingYear(1, 2025))
                .thenReturn(List.of(failed));

        FailedPaymentSummaryDto dto =
                service.getFailedPaymentsSummary(1, 2025);

        assertEquals(1, dto.failedCount());
        assertEquals(400, dto.failedAmount());
    }

    /* ================= MONTHLY OUTSTANDING (12 MONTH LOOP) ================= */

    @Test
    void getMonthlyOutstanding_success() {

        Payment p = new Payment();
        p.setStatus(PaymentStatus.SUCCESS);
        p.setAmount(500);

        when(paymentRepository.findByBillingMonthAndBillingYearAndStatus(
                anyInt(), eq(2025), eq(PaymentStatus.SUCCESS)))
                .thenReturn(List.of(p));

        when(billingClient.getTotalBilledForMonth(anyInt(), eq(2025)))
                .thenReturn(1000.0);

        List<MonthlyOutstandingDto> result =
                service.getMonthlyOutstanding(2025);

        assertEquals(12, result.size());
        assertEquals(500, result.get(0).outstandingAmount());
    }

    /* ================= REVENUE BY MODE ================= */

    @Test
    void getRevenueByMode_success() {

        Payment p = new Payment();
        p.setAmount(600);
        p.setStatus(PaymentStatus.SUCCESS);
        p.setMode(PaymentMode.ONLINE);

        when(paymentRepository.findByBillingMonthAndBillingYear(1, 2025))
                .thenReturn(List.of(p));

        List<PaymentModeSummaryDto> result =
                service.getRevenueByMode(1, 2025);

        assertEquals(1, result.size());
        assertEquals(600, result.get(0).amount());
    }

    /* ================= CONSUMER PAYMENT SUMMARY ================= */

    @Test
    void getConsumerPaymentSummary_success() {

        Payment p = new Payment();
        p.setConsumerId("C1");
        p.setStatus(PaymentStatus.SUCCESS);
        p.setAmount(700);

        when(paymentRepository.findByBillingMonthAndBillingYear(1, 2025))
                .thenReturn(List.of(p));

        List<ConsumerPaymentSummaryDto> result =
                service.getConsumerPaymentSummary(1, 2025);

        assertEquals(1, result.size());
        assertEquals("C1", result.get(0).consumerId());
        assertEquals(700, result.get(0).totalPaid());
    }

    /* ================= YEARLY REVENUE ================= */

    @Test
    void getYearlyRevenue_success() {

        Payment p = new Payment();
        p.setBillingMonth(1);
        p.setBillingYear(2025);
        p.setStatus(PaymentStatus.SUCCESS);
        p.setAmount(800);

        when(paymentRepository.findByBillingYear(2025))
                .thenReturn(List.of(p));

        List<RevenueSummaryDto> result =
                service.getYearlyRevenue(2025);

        assertEquals(1, result.size());
        assertEquals(800, result.get(0).totalRevenue());
    }
}
