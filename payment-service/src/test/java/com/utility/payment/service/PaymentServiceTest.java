package com.utility.payment.service;

import com.utility.payment.config.RabbitMQConfig;
import com.utility.payment.dto.*;
import com.utility.payment.exception.ApiException;
import com.utility.payment.feign.BillingClient;
import com.utility.payment.feign.ConsumerClient;
import com.utility.payment.model.*;
import com.utility.payment.repository.InvoiceRepository;
import com.utility.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private BillingClient billingClient;
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private ConsumerClient consumerClient;
    @Mock private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /* ================= INITIATE ONLINE ================= */

    @Test
    void initiateOnline_success() {

        InitiateOnlinePaymentRequest req =
                new InitiateOnlinePaymentRequest("B1", "C1");

        BillResponse bill = new BillResponse();
        bill.setStatus(BillStatus.GENERATED); // ✅ FIX
        bill.setTotalAmount(1000);
        bill.setBillingMonth(1);
        bill.setBillingYear(2025);

        ConsumerResponse consumer = new ConsumerResponse();
        consumer.setEmail("test@example.com");

        when(billingClient.getBill("B1")).thenReturn(bill);
        when(paymentRepository.findByBillId("B1")).thenReturn(List.of());
        when(paymentRepository.findByBillIdAndStatus("B1", PaymentStatus.INITIATED))
                .thenReturn(Optional.empty());
        when(consumerClient.getConsumerById("C1")).thenReturn(consumer);
        when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PaymentResponse response = service.initiateOnline(req);

        assertEquals(PaymentStatus.INITIATED, response.status());

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE),
                eq(RabbitMQConfig.PAYMENT_OTP_KEY),
                any(Object.class)
        );
    }

    @Test
    void initiateOnline_billAlreadyPaid() {

        BillResponse bill = new BillResponse();
        bill.setStatus(BillStatus.PAID);

        when(billingClient.getBill("B1")).thenReturn(bill);

        ApiException ex = assertThrows(ApiException.class,
                () -> service.initiateOnline(
                        new InitiateOnlinePaymentRequest("B1", "C1")));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    /* ================= CONFIRM OTP ================= */

    @Test
    void confirmOtp_success() {

        Payment payment = new Payment();
        payment.setId("P1");
        payment.setBillId("B1");
        payment.setOtp("123456");
        payment.setOtpExpiry(Instant.now().plusSeconds(60));
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setAmount(500);
        payment.setCreatedAt(LocalDateTime.now());

        BillResponse bill = new BillResponse();
        bill.setBillingMonth(1);
        bill.setBillingYear(2025);
        bill.setEnergyCharge(400);
        bill.setTax(50);
        bill.setPenalty(50);

        when(paymentRepository.findById("P1")).thenReturn(Optional.of(payment));
        when(billingClient.getBill("B1")).thenReturn(bill);
        doNothing().when(billingClient).markPaid("B1");
        when(invoiceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PaymentResponse response =
                service.confirmOtp(new ConfirmOtpRequest("P1", "123456"));

        assertEquals(PaymentStatus.SUCCESS, response.status());
    }

    @Test
    void confirmOtp_invalidOtp() {

        Payment payment = new Payment();
        payment.setOtp("123456");
        payment.setOtpExpiry(Instant.now().plusSeconds(60));
        payment.setStatus(PaymentStatus.INITIATED);

        when(paymentRepository.findById("P1"))
                .thenReturn(Optional.of(payment));

        assertThrows(ApiException.class,
                () -> service.confirmOtp(
                        new ConfirmOtpRequest("P1", "999999")));
    }

    /* ================= OFFLINE PAYMENT ================= */

    @Test
    void offlinePayment_success() {

        OfflinePaymentRequest req =
                new OfflinePaymentRequest("B1", "C1", "cash");

        BillResponse bill = new BillResponse();
        bill.setStatus(BillStatus.GENERATED);
        bill.setTotalAmount(500);
        bill.setBillingMonth(1);
        bill.setBillingYear(2025);
        bill.setEnergyCharge(400);
        bill.setTax(50);
        bill.setPenalty(50);

        when(billingClient.getBill("B1")).thenReturn(bill);
        doNothing().when(billingClient).markPaid("B1");
        when(paymentRepository.findByBillId("B1")).thenReturn(List.of());
        when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(invoiceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PaymentResponse response = service.offlinePayment(req);

        assertEquals(PaymentStatus.SUCCESS, response.status());
    }

    /* ================= GET PAYMENTS ================= */

    @Test
    void getPaymentsByBill_success() {

        Payment p = new Payment();
        p.setBillId("B1");

        BillResponse bill = new BillResponse();
        bill.setBillingMonth(1);
        bill.setBillingYear(2025);

        when(paymentRepository.findByBillId("B1"))
                .thenReturn(List.of(p));
        when(billingClient.getBill("B1")).thenReturn(bill);

        List<PaymentResponse> list =
                service.getPaymentsByBill("B1");

        assertEquals(1, list.size());
    }

    @Test
    void getPaymentsByConsumer_success() {

        Payment p = new Payment();
        p.setConsumerId("C1");
        p.setBillId("B1");

        BillResponse bill = new BillResponse();
        bill.setBillingMonth(1);
        bill.setBillingYear(2025);

        when(paymentRepository.findByConsumerIdOrderByCreatedAtDesc("C1"))
                .thenReturn(List.of(p));
        when(billingClient.getBill(anyString()))
                .thenReturn(bill);

        List<PaymentResponse> list =
                service.getPaymentsByConsumer("C1");

        assertEquals(1, list.size());
    }

    @Test
    void getAllPayments_success() {

        Payment p = new Payment();
        p.setBillId("B1");

        BillResponse bill = new BillResponse();
        bill.setBillingMonth(1);
        bill.setBillingYear(2025);

        when(paymentRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(p));
        when(billingClient.getBill("B1"))
                .thenReturn(bill);

        List<PaymentResponse> list = service.getAllPayments();

        assertEquals(1, list.size());
    }

    /* ================= OUTSTANDING ================= */

    @Test
    void getOutstanding_success() {

        BillResponse bill = new BillResponse();
        bill.setTotalAmount(1000);

        Payment p = new Payment();
        p.setAmount(400);
        p.setStatus(PaymentStatus.SUCCESS);

        when(billingClient.getBill("B1")).thenReturn(bill);
        when(paymentRepository.findByBillId("B1"))
                .thenReturn(List.of(p));

        OutstandingResponse response = service.getOutstanding("B1");

        assertEquals(600, response.getOutstandingAmount());
    }
}