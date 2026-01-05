package com.utility.billing.config;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

            // ✅ SEED ONLY IF EMPTY
            if (billRepo.count() > 0) {
                System.out.println(" Bills already exist. Skipping bill seeding.");
                return;
            }

            System.out.println("🌱 Seeding Bills from meter readings...");

            List<ConsumerConnectionResponse> connections =
                    connectionClient.getAllConnections();

            for (ConsumerConnectionResponse conn : connections) {

                if (!conn.isActive()) continue;

                List<MeterReadingResponse> readings;

                try {
                    readings = meterClient.getByConnection(conn.getId());

                    if (readings == null || readings.isEmpty()) {
                        System.out.println("⚠ No meter readings for connection");
                        continue;
                    }
                } catch (Exception e) {
                    System.out.println(
                            "Failed to fetch meter readings for connection"
                    );
                    e.printStackTrace();
                    continue;
                }

                for (MeterReadingResponse reading : readings) {

                    boolean exists =
                            billRepo.existsByConnectionIdAndBillingMonthAndBillingYear(
                                    conn.getId(),
                                    reading.getReadingMonth(),
                                    reading.getReadingYear()
                            );

                    if (exists) continue;

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

                    // ✅ Bill date = 25th of reading month
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

                    billRepo.save(bill);
                }
            }

            System.out.println("✅ Bill seeding completed");
        };
    }

    /* ---------------- HELPERS ---------------- */

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

            if (remaining <= 0) break;

            long slabUnits =
                    Math.min(
                            remaining,
                            slab.getMaxUnits() - slab.getMinUnits() + 1
                    );

            amount += slabUnits * slab.getRate();
            remaining -= slabUnits;
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

    /**
     * ✅ REALISTIC STATUS DISTRIBUTION
     */
    private BillStatus calculateStatus(LocalDate dueDate) {

        LocalDate today = LocalDate.now();

        if (today.isBefore(dueDate)) {
            return BillStatus.DUE;
        }

        long overdueDays = ChronoUnit.DAYS.between(dueDate, today);

        // Old bills → mostly PAID
        if (overdueDays > 60) {
            return Math.random() < 0.9
                    ? BillStatus.PAID
                    : BillStatus.OVERDUE;
        }

        // Recent overdue → mix
        return Math.random() < 0.6
                ? BillStatus.OVERDUE
                : BillStatus.PAID;
    }
}