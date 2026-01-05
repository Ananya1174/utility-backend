package com.utility.billing.service;

import com.utility.billing.dto.BillResponse;
import com.utility.billing.dto.dashboard.*;
import com.utility.billing.model.Bill;
import com.utility.billing.model.BillStatus;
import com.utility.billing.model.UtilityType;
import com.utility.billing.repository.BillRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingDashboardServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private BillingService billingService;

    @InjectMocks
    private BillingDashboardService service;

    private Bill bill(String consumer, UtilityType type, BillStatus status, double amount, long units) {
        Bill b = new Bill();
        b.setConsumerId(consumer);
        b.setUtilityType(type);
        b.setStatus(status);
        b.setTotalAmount(amount);
        b.setConsumptionUnits(units);
        return b;
    }

    @Test
    void getBillsSummary_success() {

        when(billRepository.countByBillingMonthAndBillingYear(1, 2025))
                .thenReturn(10L);
        when(billRepository.countByBillingMonthAndBillingYearAndStatus(1, 2025, BillStatus.PAID))
                .thenReturn(6L);

        BillsSummaryDto dto = service.getBillsSummary(1, 2025);

        assertEquals(10, dto.getTotalBills());
        assertEquals(6, dto.getPaidBills());
        assertEquals(4, dto.getUnpaidBills());
    }

    @Test
    void getConsumptionSummary_success() {

        when(billRepository.findByBillingMonthAndBillingYear(1, 2025))
                .thenReturn(List.of(
                        bill("C1", UtilityType.ELECTRICITY, BillStatus.PAID, 200, 100),
                        bill("C2", UtilityType.ELECTRICITY, BillStatus.PAID, 300, 150)
                ));

        List<ConsumptionSummaryDto> result =
                service.getConsumptionSummary(1, 2025);

        assertEquals(1, result.size());
        assertEquals(250L, result.get(0).getTotalUnits());
    }

    @Test
    void getAverageConsumption_success() {

        when(billRepository.findByBillingMonthAndBillingYear(1, 2025))
                .thenReturn(List.of(
                        bill("C1", UtilityType.WATER, BillStatus.PAID, 100, 50),
                        bill("C2", UtilityType.WATER, BillStatus.PAID, 200, 150)
                ));

        List<AverageConsumptionDto> result =
                service.getAverageConsumption(1, 2025);

        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getAverageUnits());
    }

    @Test
    void getConsumerBillingSummary_success() {

        when(billRepository.findByBillingMonthAndBillingYear(1, 2025))
                .thenReturn(List.of(
                        bill("C1", UtilityType.ELECTRICITY, BillStatus.PAID, 200, 100),
                        bill("C1", UtilityType.ELECTRICITY, BillStatus.DUE, 300, 150)
                ));

        List<ConsumerBillingSummaryDto> result =
                service.getConsumerBillingSummary(1, 2025);

        assertEquals(1, result.size());
        assertEquals(500, result.get(0).getTotalAmount());
        assertEquals(200, result.get(0).getPaidAmount());
    }

    @Test
    void getConsumerBillingHistory_success() {

        when(billingService.getBillsByConsumer("C1"))
                .thenReturn(List.of(new BillResponse()));

        assertEquals(1, service.getConsumerBillingHistory("C1").size());
    }

    @Test
    void getTotalBilledForMonth_success() {

        when(billRepository.findByBillingMonthAndBillingYear(1, 2025))
                .thenReturn(List.of(
                        bill("C1", UtilityType.GAS, BillStatus.PAID, 500, 100)
                ));

        assertEquals(500, service.getTotalBilledForMonth(1, 2025));
    }
}