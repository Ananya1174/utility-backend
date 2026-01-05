package com.utility.billing.scheduler;

import com.utility.billing.model.Bill;
import com.utility.billing.model.BillStatus;
import com.utility.billing.repository.BillRepository;
import com.utility.billing.event.BillEventPublisher;
import com.utility.billing.feign.ConsumerClient;
import com.utility.common.dto.event.BillDueReminderEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BillReminderScheduler {

    private final BillRepository billRepository;
    private final ConsumerClient consumerClient;
    private final BillEventPublisher billEventPublisher;

    
    @Scheduled(cron = "0 0 9 * * ?") // every minute for testing 0 */1 * * * ?
    public void sendDueReminders() {

        LocalDate start = LocalDate.now().plusDays(2);
        LocalDate end   = start.plusDays(1); // handles timezone safely

        List<Bill> bills =
            billRepository.findByStatusAndDueDateBetween(
                BillStatus.DUE,
                start,
                end
            );


        for (Bill bill : bills) {
            try {
                var consumer = consumerClient.getConsumerById(bill.getConsumerId());

                BillDueReminderEvent event = new BillDueReminderEvent();
                event.setBillId(bill.getId());
                event.setConsumerId(bill.getConsumerId());
                event.setEmail(consumer.getEmail());
                event.setAmount(bill.getTotalAmount());
                event.setDueDate(bill.getDueDate().toString());
                event.setUtilityType(bill.getUtilityType().name());

                billEventPublisher.publishDueReminder(event);


            } catch (Exception e) {
                System.err.println("Reminder skipped for bill " + bill.getId());
                e.printStackTrace();
            }
        }
    }
}