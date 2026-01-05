package com.utility.consumer.service;

import com.utility.consumer.dto.dashboard.ConsumerDashboardSummaryDto;
import com.utility.consumer.enums.ConnectionRequestStatus;
import com.utility.consumer.feign.BillResponseDto;
import com.utility.consumer.feign.BillingClient;
import com.utility.consumer.feign.PaymentClient;
import com.utility.consumer.feign.PaymentResponseDto;
import com.utility.consumer.model.ConnectionRequest;
import com.utility.consumer.model.UtilityConnection;
import com.utility.consumer.repository.ConnectionRepository;
import com.utility.consumer.repository.ConnectionRequestRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsumerDashboardServiceTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private ConnectionRequestRepository requestRepository;

    @Mock
    private BillingClient billingClient;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private ConsumerDashboardService service;

    @Test
    void getDashboardSummary_withBillsAndPayments() {

        UtilityConnection conn = new UtilityConnection();
        conn.setActive(true);

        when(connectionRepository.findByConsumerId("C1"))
                .thenReturn(List.of(conn));

        ConnectionRequest req = new ConnectionRequest();
        req.setConsumerId("C1");
        req.setStatus(ConnectionRequestStatus.PENDING);

        when(requestRepository.findByStatus(ConnectionRequestStatus.PENDING))
                .thenReturn(List.of(req));

        BillResponseDto bill = new BillResponseDto();
        bill.setStatus("UNPAID");
        bill.setPayableAmount(500);

        when(billingClient.getBillsByConsumer("C1"))
                .thenReturn(List.of(bill));

        PaymentResponseDto payment = new PaymentResponseDto();
        payment.setAmount(500);
        payment.setConfirmedAt(LocalDateTime.now());

        when(paymentClient.getPaymentsByConsumer("C1"))
                .thenReturn(List.of(payment));

        ConsumerDashboardSummaryDto dto =
                service.getDashboardSummary("C1");

        assertEquals(1, dto.activeUtilities());
    }
}