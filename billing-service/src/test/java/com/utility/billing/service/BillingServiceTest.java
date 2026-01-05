package com.utility.billing.service;

import com.utility.billing.dto.BillResponse;
import com.utility.billing.dto.GenerateBillRequest;
import com.utility.billing.event.BillEventPublisher;
import com.utility.billing.exception.ApiException;
import com.utility.billing.feign.*;
import com.utility.billing.model.*;
import com.utility.billing.repository.BillRepository;
import com.utility.billing.repository.TariffSlabRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private MeterReadingClient meterClient;

    @Mock
    private TariffSlabRepository slabRepository;

    @Mock
    private BillEventPublisher publisher;

    @Mock
    private ConnectionClient connectionClient;

    @Mock
    private ConsumerClient consumerClient;

    @InjectMocks
    private BillingService service;

    // ------------------ helpers ------------------

    private GenerateBillRequest request() {
        GenerateBillRequest r = new GenerateBillRequest();
        r.setConsumerId("C1");
        r.setConnectionId("CON1");
        return r;
    }

    /**
     * ⭐ Centralized mock setup for generateBill
     * Used by MULTIPLE tests
     */
    private void mockGenerateBillDependencies() {

        // Meter reading
        MeterReadingResponse reading = new MeterReadingResponse();
        reading.setReadingMonth(LocalDate.now().getMonthValue());
        reading.setReadingYear(LocalDate.now().getYear());
        reading.setConsumptionUnits(100);
        reading.setUtilityType(UtilityType.ELECTRICITY);

        when(meterClient.getLatest("CON1"))
                .thenReturn(reading);

        when(billRepository
                .findTopByConnectionIdOrderByBillingYearDescBillingMonthDesc("CON1"))
                .thenReturn(Optional.empty());

        // Connection
        ConsumerConnectionResponse conn = new ConsumerConnectionResponse();
        conn.setActive(true);
        conn.setUtilityType(UtilityType.ELECTRICITY);
        conn.setTariffPlan("DOMESTIC");

        when(connectionClient.getConnectionById("CON1"))
                .thenReturn(conn);

        // Consumer
        ConsumerResponse consumer = new ConsumerResponse();
        consumer.setId("C1");
        consumer.setEmail("a@gmail.com");

        when(consumerClient.getConsumerById("C1"))
                .thenReturn(consumer);

        // Tariff slab
        TariffSlab slab = new TariffSlab();
        slab.setMinUnits(0);
        slab.setMaxUnits(200);
        slab.setRate(5);

        when(slabRepository
                .findByUtilityTypeAndPlanCodeOrderByMinUnitsAsc(
                        UtilityType.ELECTRICITY, "DOMESTIC"))
                .thenReturn(List.of(slab));

        when(billRepository.save(any(Bill.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    // ------------------ tests ------------------

    @Test
    void generateBill_success() {

        mockGenerateBillDependencies();

        BillResponse response = service.generateBill(request());

        assertNotNull(response);
        assertTrue(response.getTotalAmount() > 0);

        verify(publisher, times(1))
                .publish(any());
    }
    @Test
    void markBillAsPaid_notFound_lambdaCovered() {

        when(billRepository.findById("B99"))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.markBillAsPaid("B99"));
    }
    @Test
    void generateBill_multipleSlabs_lambdaCovered() {

        mockGenerateBillDependencies();

        TariffSlab slab1 = new TariffSlab();
        slab1.setMinUnits(0);
        slab1.setMaxUnits(50);
        slab1.setRate(3);

        TariffSlab slab2 = new TariffSlab();
        slab2.setMinUnits(51);
        slab2.setMaxUnits(200);
        slab2.setRate(5);

        when(slabRepository.findByUtilityTypeAndPlanCodeOrderByMinUnitsAsc(
                UtilityType.ELECTRICITY, "DOMESTIC"))
                .thenReturn(List.of(slab1, slab2));

        BillResponse response = service.generateBill(request());

        assertTrue(response.getEnergyCharge() > 0);
    }
    @Test
    void getAllBills_byMonthYear() {

        when(billRepository.findByBillingMonthAndBillingYear(1, 2025))
                .thenReturn(List.of(new Bill()));

        assertEquals(1,
                service.getAllBills(null, 1, 2025, null).size());
    }

    @Test
    void getAllBills_byConsumerId() {

        when(billRepository.findByConsumerId("C1"))
                .thenReturn(List.of(new Bill()));

        assertEquals(1,
                service.getAllBills(null, null, null, "C1").size());
    }

    @Test
    void getAllBills_all() {

        when(billRepository.findAll())
                .thenReturn(List.of(new Bill()));

        assertEquals(1,
                service.getAllBills(null, null, null, null).size());
    }
    @Test
    void getBillsByConsumer_success() {

        when(billRepository.findByConsumerId("C1"))
                .thenReturn(List.of(new Bill()));

        assertEquals(1,
                service.getBillsByConsumer("C1").size());
    }

    @Test
    void generateBill_lambdaCovered() {

        mockGenerateBillDependencies(); // ⭐ REQUIRED

        BillResponse response = service.generateBill(request());

        // ⭐ forces lambda execution for JaCoCo
        assertTrue(response.getTotalAmount() > 0);
    }

    @Test
    void markBillAsPaid_success() {

        Bill bill = new Bill();
        bill.setStatus(BillStatus.DUE);
        bill.setEnergyCharge(100);
        bill.setFixedCharge(50);
        bill.setTax(10);
        bill.setPenalty(0);

        when(billRepository.findById("B1"))
                .thenReturn(Optional.of(bill));

        service.markBillAsPaid("B1");

        assertEquals(BillStatus.PAID, bill.getStatus());
        assertEquals(160, bill.getTotalAmount());
    }

    @Test
    void markBillAsPaid_withPenalty() {

        Bill bill = new Bill();
        bill.setStatus(BillStatus.DUE);
        bill.setPenalty(50);

        when(billRepository.findById("B1"))
                .thenReturn(Optional.of(bill));

        service.markBillAsPaid("B1");

        assertEquals(BillStatus.PAID, bill.getStatus());
    }

    @Test
    void getBillById_notFound() {

        when(billRepository.findById("B1"))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.getBillById("B1"));
    }

    @Test
    void getBillsByConsumer_notFound() {

        when(billRepository.findByConsumerId("C1"))
                .thenReturn(List.of());

        assertThrows(ApiException.class,
                () -> service.getBillsByConsumer("C1"));
    }

    @Test
    void getAllBills_withStatus() {

        when(billRepository.findByStatus(BillStatus.PAID))
                .thenReturn(List.of(new Bill()));

        assertEquals(
                1,
                service.getAllBills(BillStatus.PAID, null, null, null).size()
        );
    }

    @Test
    void getTotalBilledAmount_success() {

        Bill b = new Bill();
        b.setTotalAmount(500);

        when(billRepository.findAll())
                .thenReturn(List.of(b));

        assertEquals(500, service.getTotalBilledAmount());
    }

    @Test
    void getOverdueBills_success() {

        when(billRepository.findByStatus(BillStatus.OVERDUE))
                .thenReturn(List.of(new Bill()));

        assertEquals(1, service.getOverdueBills().size());
    }

    @Test
    void map_billCovered() {

        Bill bill = new Bill();
        bill.setId("B1");
        bill.setConsumerId("C1");
        bill.setTotalAmount(500);

        when(billRepository.findAll())
                .thenReturn(List.of(bill));

        List<BillResponse> result =
                service.getAllBills(null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("B1", result.get(0).getId());
    }

    @Test
    void meterFallback_called() {

        MeterReadingResponse response =
                service.meterFallback("CON1", new RuntimeException());

        assertNull(response);
    }
}