package com.utility.billing.scheduler;

import com.utility.billing.model.Bill;
import com.utility.billing.model.BillStatus;
import com.utility.billing.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BillingOverdueScheduler {

    private final BillRepository billRepository;

    // Runs every day at 12:05 AM
    @Scheduled(cron = "0 5 0 * * ?") //0 */1 * * * ? every minute for testing
    public void markOverdueBills() {

        List<Bill> dueBills = billRepository.findByStatus(BillStatus.DUE);
        LocalDate today = LocalDate.now();

        for (Bill bill : dueBills) {
            if (bill.getDueDate().isBefore(today)) {

                bill.setStatus(BillStatus.OVERDUE);

                bill.setPenalty(
                        bill.getPenalty() + 50
                );

                billRepository.save(bill);
            }
        }
    }
}