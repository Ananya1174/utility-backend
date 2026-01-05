package com.utility.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.utility.auth.dto.request.AccountRequestDto;
import com.utility.auth.dto.request.AccountRequestReviewDto;
import com.utility.auth.event.NotificationPublisher;
import com.utility.auth.model.AccountRequest;
import com.utility.auth.model.AccountRequestStatus;
import com.utility.auth.repository.AccountRequestRepository;
import com.utility.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AccountRequestServiceTest {

    @Mock
    private AccountRequestRepository accountRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private AccountRequestService accountRequestService;

    // ---------------- CREATE ----------------
    @Test
    void createAccountRequest_success() {

        AccountRequestDto dto = new AccountRequestDto();
        dto.setName("Ananya");
        dto.setEmail("a@gmail.com");
        dto.setPhone("9999999999");
        dto.setAddress("BLR");

        when(accountRequestRepository.existsByEmail("a@gmail.com"))
                .thenReturn(false);

        when(accountRequestRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        AccountRequest request =
                accountRequestService.createAccountRequest(dto);

        assertEquals(AccountRequestStatus.PENDING, request.getStatus());
    }

    // ---------------- GET PENDING ----------------
    @Test
    void getPendingRequests_success() {

        when(accountRequestRepository.findByStatus(AccountRequestStatus.PENDING))
                .thenReturn(List.of());

        assertNotNull(accountRequestService.getPendingRequests());
    }

    // ---------------- REVIEW APPROVE ----------------
    @Test
    void reviewAccountRequest_approve() {

        AccountRequest request = AccountRequest.builder()
                .requestId("R1")
                .email("a@gmail.com")
                .name("Ananya")
                .phone("999")
                .address("BLR")
                .status(AccountRequestStatus.PENDING)
                .build();

        AccountRequestReviewDto dto = new AccountRequestReviewDto();
        dto.setRequestId("R1");
        dto.setDecision("APPROVE");

        when(accountRequestRepository.findById("R1"))
                .thenReturn(Optional.of(request));

        when(passwordEncoder.encode(any()))
                .thenReturn("encoded");

        accountRequestService.reviewAccountRequest(dto);

        verify(notificationPublisher)
                .publishAccountApproved(any());
    }
    @Test
    void reviewAccountRequest_alreadyReviewed() {
        AccountRequest req = AccountRequest.builder()
                .status(AccountRequestStatus.APPROVED)
                .build();

        when(accountRequestRepository.findById("R1"))
                .thenReturn(Optional.of(req));

        AccountRequestReviewDto dto = new AccountRequestReviewDto();
        dto.setRequestId("R1");
        dto.setDecision("APPROVE");

        assertThrows(RuntimeException.class,
                () -> accountRequestService.reviewAccountRequest(dto));
    }
    @Test
    void reviewAccountRequest_invalidDecision() {
        AccountRequest req = AccountRequest.builder()
                .status(AccountRequestStatus.PENDING)
                .build();

        when(accountRequestRepository.findById("R1"))
                .thenReturn(Optional.of(req));

        AccountRequestReviewDto dto = new AccountRequestReviewDto();
        dto.setRequestId("R1");
        dto.setDecision("INVALID");

        assertThrows(RuntimeException.class,
                () -> accountRequestService.reviewAccountRequest(dto));
    }

    // ---------------- REVIEW REJECT ----------------
    @Test
    void reviewAccountRequest_reject() {

        AccountRequest request = AccountRequest.builder()
                .requestId("R1")
                .email("a@gmail.com")
                .status(AccountRequestStatus.PENDING)
                .build();

        AccountRequestReviewDto dto = new AccountRequestReviewDto();
        dto.setRequestId("R1");
        dto.setDecision("REJECT");

        when(accountRequestRepository.findById("R1"))
                .thenReturn(Optional.of(request));

        accountRequestService.reviewAccountRequest(dto);

        verify(notificationPublisher)
                .publishAccountRejected(any());
    }
}