package com.utility.consumer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.utility.consumer.dto.dashboard.ConsumerDashboardSummaryDto;
import com.utility.consumer.enums.ConnectionRequestStatus;
import com.utility.consumer.feign.BillingClient;
import com.utility.consumer.feign.PaymentClient;
import com.utility.consumer.model.UtilityConnection;
import com.utility.consumer.repository.ConnectionRepository;
import com.utility.consumer.repository.ConnectionRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsumerDashboardService {

    private final ConnectionRepository connectionRepository;
    private final ConnectionRequestRepository requestRepository;
    private final BillingClient billingClient;
    private final PaymentClient paymentClient;

    public ConsumerDashboardSummaryDto getDashboardSummary(String consumerId) {

        List<UtilityConnection> connections =
                connectionRepository.findByConsumerId(consumerId);

        int activeUtilities = (int) connections.stream()
                .filter(UtilityConnection::isActive)
                .count();

        int pendingRequests = requestRepository
                .findByStatus(ConnectionRequestStatus.PENDING)
                .stream()
                .filter(r -> r.getConsumerId().equals(consumerId))
                .toList()
                .size();

        var bills = billingClient.getBillsByConsumer(consumerId);

        int totalBills = bills.size();

        int unpaidBills = (int) bills.stream()
                .filter(b -> !"PAID".equalsIgnoreCase(b.getStatus()))
                .count();

        double totalOutstanding = bills.stream()
                .filter(b -> !"PAID".equalsIgnoreCase(b.getStatus()))
                .mapToDouble(b -> b.getPayableAmount())
                .sum();

       
        var payments = paymentClient.getPaymentsByConsumer(consumerId);

        var lastPayment = payments.stream()
                .filter(p -> p.getConfirmedAt() != null)
                .findFirst()
                .orElse(null);

        return new ConsumerDashboardSummaryDto(
                activeUtilities,
                pendingRequests,
                totalBills,
                unpaidBills,
                totalOutstanding,
                lastPayment != null ? lastPayment.getAmount() : null,
                lastPayment != null ? lastPayment.getConfirmedAt() : null
        );
    }
}