package com.utility.billing.service;

import java.util.List;
import java.util.stream.Collectors;

import com.utility.billing.dto.BillResponse;
import com.utility.billing.dto.dashboard.AverageConsumptionDto;
import com.utility.billing.dto.dashboard.BillsSummaryDto;
import com.utility.billing.dto.dashboard.ConsumerBillingSummaryDto;
import com.utility.billing.dto.dashboard.ConsumptionSummaryDto;
import com.utility.billing.model.Bill;
import com.utility.billing.model.BillStatus;
import com.utility.billing.repository.BillRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingDashboardService {

    private final BillRepository billRepository;
    private final BillingService billingService;

    /* ================= DASHBOARD ================= */

    public BillsSummaryDto getBillsSummary(int month, int year) {

        long total = billRepository
                .countByBillingMonthAndBillingYear(month, year);

        long paid = billRepository
                .countByBillingMonthAndBillingYearAndStatus(
                        month, year, BillStatus.PAID);

        return new BillsSummaryDto(
                month,
                year,
                total,
                paid,
                total - paid
        );
    }

    public List<ConsumptionSummaryDto> getConsumptionSummary(
            int month, int year) {

        return billRepository.findByBillingMonthAndBillingYear(month, year)
                .stream()
                .collect(Collectors.groupingBy(
                        Bill::getUtilityType,
                        Collectors.summingLong(Bill::getConsumptionUnits)
                ))
                .entrySet()
                .stream()
                .map(e -> new ConsumptionSummaryDto(
                        e.getKey(), e.getValue()))
                .toList();
    }

    public List<AverageConsumptionDto> getAverageConsumption(
            int month, int year) {

        return billRepository.findByBillingMonthAndBillingYear(month, year)
                .stream()
                .collect(Collectors.groupingBy(
                        Bill::getUtilityType,
                        Collectors.averagingLong(Bill::getConsumptionUnits)
                ))
                .entrySet()
                .stream()
                .map(e -> new AverageConsumptionDto(
                        e.getKey(), e.getValue()))
                .toList();
    }

    /* ================= REPORTS ================= */

    public List<ConsumerBillingSummaryDto> getConsumerBillingSummary(
            int month, int year) {

        return billRepository.findByBillingMonthAndBillingYear(month, year)
                .stream()
                .collect(Collectors.groupingBy(Bill::getConsumerId))
                .entrySet()
                .stream()
                .map(e -> {

                    List<Bill> bills = e.getValue();

                    double total = bills.stream()
                            .mapToDouble(Bill::getTotalAmount)
                            .sum();

                    double paid = bills.stream()
                            .filter(b -> b.getStatus() == BillStatus.PAID)
                            .mapToDouble(Bill::getTotalAmount)
                            .sum();

                    return new ConsumerBillingSummaryDto(
                            e.getKey(),
                            bills.size(),
                            total,
                            paid,
                            total - paid
                    );
                })
                .toList();
    }

    public List<BillResponse> getConsumerBillingHistory(String consumerId) {
        return billingService.getBillsByConsumer(consumerId);
    }
    public double getTotalBilledForMonth(int month, int year) {
        return billRepository
                .findByBillingMonthAndBillingYear(month, year)
                .stream()
                .mapToDouble(Bill::getTotalAmount)
                .sum();
    }

    /* ================= MAPPER ================= */

    private BillResponse map(Bill bill) {

        BillResponse r = new BillResponse();
        r.setId(bill.getId());
        r.setConsumerId(bill.getConsumerId());
        r.setConnectionId(bill.getConnectionId());
        r.setUtilityType(bill.getUtilityType());
        r.setTariffPlan(bill.getTariffPlan());
        r.setBillingMonth(bill.getBillingMonth());
        r.setBillingYear(bill.getBillingYear());
        r.setConsumptionUnits(bill.getConsumptionUnits());
        r.setTax(bill.getTax());
        r.setPenalty(bill.getPenalty());
        r.setTotalAmount(bill.getTotalAmount());
        r.setPayableAmount(bill.getTotalAmount() + bill.getPenalty());
        r.setStatus(bill.getStatus());
        r.setDueDate(bill.getDueDate());
        return r;
    }
}