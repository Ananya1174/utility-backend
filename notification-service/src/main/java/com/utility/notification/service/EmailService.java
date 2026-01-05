package com.utility.notification.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.utility.common.dto.event.BillDueReminderEvent;
import com.utility.common.dto.event.BillGeneratedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendCredentialsEmail(
            String to,
            String username,
            String tempPassword) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Your Utility Billing Account Credentials");

        message.setText("""
                Hello,

                Your account has been approved.

                Username: %s
                Temporary Password: %s

                Please login and change your password immediately.

                Regards,
                Utility Billing System
                """.formatted(username, tempPassword));

        mailSender.send(message);
    }
    public void sendRejectionEmail(String to) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Account Request Update");

        message.setText("""
                Hello,

                Your account request has been reviewed.
                Unfortunately, it was not approved at this time.

                Please contact support for further assistance.

                Regards,
                Utility Billing System
                """);

        mailSender.send(message);
    }
     public void sendBillEmail(String to, BillGeneratedEvent event) {

        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Email is missing in BillGeneratedEvent");
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("charliektest11@gmail.com");
        message.setTo(to);
        message.setSubject("Your Utility Bill Has Been Generated");

        message.setText("""
                Hello,

                Your utility bill has been generated successfully.

                Bill ID      : %s
                Utility Type : %s
                Tariff Plan  : %s
                Amount       : ₹%.2f
                Due Date     : %s

                Please pay before the due date to avoid penalties.

                Regards,
                Utility Billing System
                """.formatted(
                    event.getBillId(),
                    event.getUtilityType(),
                    event.getTariffPlan(),
                    event.getAmount(),
                    event.getDueDate()
                ));

        mailSender.send(message);
    }
    public void sendPaymentReminderEmail(
            String to,
            BillDueReminderEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Payment Due Reminder – Utility Bill");

        message.setText("""
                Hello,

                This is a reminder that your utility bill payment is due soon.

                Bill ID      : %s
                Utility Type : %s
                Amount       : ₹%.2f
                Due Date     : %s

                Please make the payment before the due date to avoid penalties.

                Regards,
                Utility Billing System
                """.formatted(
                    event.getBillId(),
                    event.getUtilityType(),
                    event.getAmount(),
                    event.getDueDate()
                ));

        mailSender.send(message);
    }
    public void sendPasswordResetEmail(String to, String resetToken) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Reset Your Password – Utility Billing System");

        String resetLink =
                "http://localhost:4200/reset-password?token=" + resetToken;

        message.setText("""
                Hello,

                We received a request to reset your password.

                Click the link below to reset your password:
                %s

                This link is valid for 15 minutes.
                If you did not request this, please ignore this email.

                Regards,
                Utility Billing System
                """.formatted(resetLink));

        mailSender.send(message);
    }
    public void sendPaymentOtpEmail(
            String to,
            String otp,
            int validMinutes) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("OTP for Online Payment Verification");

        message.setText("""
                Hello,

                Your One-Time Password (OTP) for confirming the online payment is:

                OTP: %s

                This OTP is valid for %d minutes.
                Please do NOT share this OTP with anyone.

                If you did not initiate this payment, please ignore this email.

                Regards,
                Utility Billing System
                """.formatted(otp, validMinutes));

        mailSender.send(message);
    }
}