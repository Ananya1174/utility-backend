package com.utility.billing.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.utility.billing.dto.BillResponse;
import com.utility.billing.dto.dashboard.AverageConsumptionDto;
import com.utility.billing.dto.dashboard.BillsSummaryDto;
import com.utility.billing.dto.dashboard.ConsumerBillingSummaryDto;
import com.utility.billing.dto.dashboard.ConsumptionSummaryDto;
import com.utility.billing.model.Bill;
import com.utility.billing.model.BillStatus;
import com.utility.billing.repository.BillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingDashboardService {

    private final BillRepository billRepository;
    private final BillingService billingService;

    public BillsSummaryDto getBillsSummary(int month, int year) {

        long total =
                billRepository.countByBillingMonthAndBillingYear(month, year);

        long paid =
                billRepository.countByBillingMonthAndBillingYearAndStatus(
                        month,
                        year,
                        BillStatus.PAID
                );

        return new BillsSummaryDto(
                month,
                year,
                total,
                paid,
                total - paid
        );
    }

    public List<ConsumptionSummaryDto> getConsumptionSummary(
            int month,
            int year
    ) {

        return billRepository
                .findByBillingMonthAndBillingYear(month, year)
                .stream()
                .collect(Collectors.groupingBy(
                        Bill::getUtilityType,
                        Collectors.summingLong(Bill::getConsumptionUnits)
                ))
                .entrySet()
                .stream()
                .map(e ->
                        new ConsumptionSummaryDto(
                                e.getKey(),
                                e.getValue()
                        )
                )
                .toList();
    }

    public List<AverageConsumptionDto> getAverageConsumption(
            int month,
            int year
    ) {

        return billRepository
                .findByBillingMonthAndBillingYear(month, year)
                .stream()
                .collect(Collectors.groupingBy(
                        Bill::getUtilityType,
                        Collectors.averagingLong(Bill::getConsumptionUnits)
                ))
                .entrySet()
                .stream()
                .map(e ->
                        new AverageConsumptionDto(
                                e.getKey(),
                                e.getValue()
                        )
                )
                .toList();
    }

    public List<ConsumerBillingSummaryDto> getConsumerBillingSummary(
            int month,
            int year
    ) {

        return billRepository
                .findByBillingMonthAndBillingYear(month, year)
                .stream()
                .collect(Collectors.groupingBy(Bill::getConsumerId))
                .entrySet()
                .stream()
                .map(e -> buildConsumerSummary(e.getKey(), e.getValue()))
                .toList();
    }

    public List<BillResponse> getConsumerBillingHistory(
            String consumerId
    ) {
        return billingService.getBillsByConsumer(consumerId);
    }

    public double getTotalBilledForMonth(int month, int year) {

        return billRepository
                .findByBillingMonthAndBillingYear(month, year)
                .stream()
                .mapToDouble(Bill::getTotalAmount)
                .sum();
    }

    private ConsumerBillingSummaryDto buildConsumerSummary(
            String consumerId,
            List<Bill> bills
    ) {

        double total =
                bills.stream()
                        .mapToDouble(Bill::getTotalAmount)
                        .sum();

        double paid =
                bills.stream()
                        .filter(b -> b.getStatus() == BillStatus.PAID)
                        .mapToDouble(Bill::getTotalAmount)
                        .sum();

        return new ConsumerBillingSummaryDto(
                consumerId,
                bills.size(),
                total,
                paid,
                total - paid
        );
    }
}