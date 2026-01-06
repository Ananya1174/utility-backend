package com.utility.billing.config;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.utility.billing.feign.ConnectionClient;
import com.utility.billing.feign.ConsumerConnectionResponse;
import com.utility.billing.feign.MeterReadingClient;
import com.utility.billing.feign.MeterReadingResponse;
import com.utility.billing.model.Bill;
import com.utility.billing.model.BillStatus;
import com.utility.billing.model.TariffSlab;
import com.utility.billing.model.UtilityType;
import com.utility.billing.repository.BillRepository;
import com.utility.billing.repository.TariffSlabRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BillingDataSeeder {

    private final BillRepository billRepo;
    private final TariffSlabRepository slabRepo;
    private final ConnectionClient connectionClient;
    private final MeterReadingClient meterClient;

    @Bean
    CommandLineRunner seedBills() {

        return args -> {

            if (billRepo.count() > 0) {
                return;
            }

            List<ConsumerConnectionResponse> connections =
                    connectionClient.getAllConnections();

            for (ConsumerConnectionResponse conn : connections) {

                if (conn.isActive()) {
                    seedBillsForConnection(conn);
                }
            }
        };
    }

    private void seedBillsForConnection(
            ConsumerConnectionResponse conn
    ) {

        List<MeterReadingResponse> readings =
                fetchReadings(conn.getId());

        if (readings.isEmpty()) {
            return;
        }

        for (MeterReadingResponse reading : readings) {

            if (!billExists(conn, reading)) {
                Bill bill = buildBill(conn, reading);
                billRepo.save(bill);
            }
        }
    }

    private List<MeterReadingResponse> fetchReadings(
            String connectionId
    ) {

        try {
            return meterClient.getByConnection(connectionId);
        } catch (FeignException ex) {
            return Collections.emptyList();
        }
    }

    private boolean billExists(
            ConsumerConnectionResponse conn,
            MeterReadingResponse reading
    ) {

        return billRepo.existsByConnectionIdAndBillingMonthAndBillingYear(
                conn.getId(),
                reading.getReadingMonth(),
                reading.getReadingYear()
        );
    }

    private Bill buildBill(
            ConsumerConnectionResponse conn,
            MeterReadingResponse reading
    ) {

        Bill bill = new Bill();

        bill.setConsumerId(conn.getConsumerId());
        bill.setConnectionId(conn.getId());
        bill.setUtilityType(conn.getUtilityType());
        bill.setTariffPlan(conn.getTariffPlan());
        bill.setBillingMonth(reading.getReadingMonth());
        bill.setBillingYear(reading.getReadingYear());
        bill.setConsumptionUnits(reading.getConsumptionUnits());

        double energyCharge =
                calculateEnergyCharge(
                        conn.getUtilityType(),
                        conn.getTariffPlan(),
                        reading.getConsumptionUnits()
                );

        double fixedCharge = fixedCharge(conn.getUtilityType());
        double tax = energyCharge * 0.05;

        LocalDate billDate =
                LocalDate.of(
                        reading.getReadingYear(),
                        reading.getReadingMonth(),
                        25
                );

        LocalDate dueDate = billDate.plusDays(15);
        BillStatus status = calculateStatus(dueDate);

        double penalty =
                status == BillStatus.OVERDUE
                        ? energyCharge * 0.10
                        : 0;

        bill.setEnergyCharge(energyCharge);
        bill.setFixedCharge(fixedCharge);
        bill.setTax(tax);
        bill.setPenalty(penalty);
        bill.setTotalAmount(
                energyCharge + fixedCharge + tax + penalty
        );
        bill.setStatus(status);
        bill.setBillDate(billDate);
        bill.setDueDate(dueDate);

        return bill;
    }

    private double calculateEnergyCharge(
            UtilityType type,
            String plan,
            long units
    ) {

        List<TariffSlab> slabs =
                slabRepo.findByUtilityTypeAndPlanCodeOrderByMinUnitsAsc(
                        type,
                        plan
                );

        double amount = 0;
        long remaining = units;

        for (TariffSlab slab : slabs) {

            long slabUnits =
                    Math.min(
                            remaining,
                            slab.getMaxUnits() - slab.getMinUnits() + 1
                    );

            if (slabUnits > 0) {
                amount += slabUnits * slab.getRate();
                remaining -= slabUnits;
            }
        }

        return amount;
    }

    private double fixedCharge(UtilityType type) {

        return switch (type) {
            case ELECTRICITY -> 50;
            case WATER -> 30;
            case GAS -> 40;
            case INTERNET -> 100;
        };
    }

    private BillStatus calculateStatus(LocalDate dueDate) {

        LocalDate today = LocalDate.now();

        if (today.isBefore(dueDate)) {
            return BillStatus.DUE;
        }

        long overdueDays =
                ChronoUnit.DAYS.between(dueDate, today);

        double chance = ThreadLocalRandom.current().nextDouble();

        if (overdueDays > 60) {
            return chance < 0.9
                    ? BillStatus.PAID
                    : BillStatus.OVERDUE;
        }

        return chance < 0.6
                ? BillStatus.OVERDUE
                : BillStatus.PAID;
    }
}