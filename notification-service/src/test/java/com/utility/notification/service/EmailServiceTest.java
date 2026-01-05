package com.utility.notification.service;

import com.utility.common.dto.event.BillDueReminderEvent;
import com.utility.common.dto.event.BillGeneratedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    EmailServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendCredentialsEmail_success() {

        emailService.sendCredentialsEmail(
                "test@example.com",
                "user123",
                "tempPass"
        );

        verify(mailSender, times(1))
                .send(any(SimpleMailMessage.class));
    }

    @Test
    void sendRejectionEmail_success() {

        emailService.sendRejectionEmail("test@example.com");

        verify(mailSender, times(1))
                .send(any(SimpleMailMessage.class));
    }

    @Test
    void sendBillEmail_success() {

        BillGeneratedEvent event = new BillGeneratedEvent();
        event.setBillId("B1");
        event.setUtilityType("ELECTRICITY");
        event.setTariffPlan("DOMESTIC");
        event.setAmount(500);
        event.setDueDate("2025-02-15");

        emailService.sendBillEmail("test@example.com", event);

        verify(mailSender, times(1))
                .send(any(SimpleMailMessage.class));
    }

    @Test
    void sendBillEmail_missingEmail_shouldThrowException() {

        BillGeneratedEvent event = new BillGeneratedEvent();
        event.setBillId("B1");

        assertThrows(
                IllegalArgumentException.class,
                () -> emailService.sendBillEmail("", event)
        );

        verify(mailSender, never())
                .send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPaymentReminderEmail_success() {

        BillDueReminderEvent event = new BillDueReminderEvent();
        event.setBillId("B1");
        event.setUtilityType("ELECTRICITY");
        event.setAmount(500);
        event.setDueDate("2025-02-20");

        emailService.sendPaymentReminderEmail(
                "test@example.com",
                event
        );

        verify(mailSender, times(1))
                .send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordResetEmail_success() {

        emailService.sendPasswordResetEmail(
                "test@example.com",
                "reset-token-123"
        );

        verify(mailSender, times(1))
                .send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPaymentOtpEmail_success() {

        emailService.sendPaymentOtpEmail(
                "test@example.com",
                "123456",
                5
        );

        verify(mailSender, times(1))
                .send(any(SimpleMailMessage.class));
    }
}
