package com.utility.auth.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.utility.auth.dto.request.AccountRequestDto;
import com.utility.auth.dto.request.AccountRequestReviewDto;
import com.utility.auth.event.NotificationPublisher;
import com.utility.auth.exception.ResourceNotFoundException;
import com.utility.auth.exception.UserAlreadyExistsException;
import com.utility.auth.model.AccountRequest;
import com.utility.auth.model.AccountRequestStatus;
import com.utility.auth.model.Role;
import com.utility.auth.model.User;
import com.utility.auth.repository.AccountRequestRepository;
import com.utility.auth.repository.UserRepository;
import com.utility.common.dto.event.AccountApprovedEvent;
import com.utility.common.dto.event.AccountRejectedEvent;
import com.utility.common.dto.event.ConsumerApprovedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountRequestService {

    private static final String DECISION_APPROVE = "APPROVE";
    private static final String DECISION_REJECT = "REJECT";
    private static final String ADMIN = "ADMIN";

    private final AccountRequestRepository accountRequestRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationPublisher notificationPublisher;

    public AccountRequest createAccountRequest(AccountRequestDto dto) {

        if (accountRequestRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Account request already exists for this email");
        }

        AccountRequest request = AccountRequest.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .status(AccountRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return accountRequestRepository.save(request);
    }

    public List<AccountRequest> getPendingRequests() {
        return accountRequestRepository.findByStatus(AccountRequestStatus.PENDING);
    }

    public void reviewAccountRequest(AccountRequestReviewDto dto) {

        AccountRequest request = accountRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Account request not found"));

        if (request.getStatus() != AccountRequestStatus.PENDING) {
            throw new BadCredentialsException("Request already reviewed");
        }

        if (DECISION_REJECT.equalsIgnoreCase(dto.getDecision())) {

            request.setStatus(AccountRequestStatus.REJECTED);
            request.setReviewedAt(LocalDateTime.now());
            request.setReviewedBy(ADMIN);

            accountRequestRepository.save(request);

            AccountRejectedEvent event = new AccountRejectedEvent();
            event.setEmail(request.getEmail());

            notificationPublisher.publishAccountRejected(event);
            return;
        }

        if (DECISION_APPROVE.equalsIgnoreCase(dto.getDecision())) {

            String username = generateUsername(request.getEmail());
            String rawPassword = generateTempPassword();

            User user = User.builder()
                    .username(username)
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(rawPassword))
                    .role(Role.CONSUMER)
                    .active(true)
                    .passwordChangeRequired(true)
                    .build();

            userRepository.save(user);

            request.setStatus(AccountRequestStatus.APPROVED);
            request.setReviewedAt(LocalDateTime.now());
            request.setReviewedBy(ADMIN);
            accountRequestRepository.save(request);

            notificationPublisher.publishAccountApproved(
                    AccountApprovedEvent.builder()
                            .email(request.getEmail())
                            .username(username)
                            .temporaryPassword(rawPassword)
                            .role(Role.CONSUMER.name())
                            .build()
            );

            notificationPublisher.publishConsumerApproved(
                    ConsumerApprovedEvent.builder()
                            .id(user.getUserId())
                            .fullName(request.getName())
                            .email(request.getEmail())
                            .mobileNumber(request.getPhone())
                            .address(request.getAddress())
                            .build()
            );

            return;
        }

        throw new IllegalArgumentException("Invalid decision. Use APPROVE or REJECT");
    }

    private String generateUsername(String email) {
        return email.split("@")[0];
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}