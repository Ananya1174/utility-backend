package com.utility.consumer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.utility.consumer.dto.request.ApproveConnectionRequestDto;
import com.utility.consumer.dto.request.CreateConnectionRequestDto;
import com.utility.consumer.enums.ConnectionRequestStatus;
import com.utility.consumer.exception.ApiException;
import com.utility.consumer.model.ConnectionRequest;
import com.utility.consumer.model.UtilityConnection;
import com.utility.consumer.repository.ConnectionRepository;
import com.utility.consumer.repository.ConnectionRequestRepository;
import com.utility.consumer.repository.ConsumerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectionRequestService {

    private final ConnectionRequestRepository requestRepo;
    private final ConnectionRepository connectionRepo;
    private final ConsumerRepository consumerRepo;

    public void createRequest(
            String consumerId,
            CreateConnectionRequestDto dto
    ) {

        if (!consumerRepo.existsById(consumerId)) {
            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    "Consumer not found"
            );
        }

        if (connectionRepo.findByConsumerIdAndUtilityType(
                consumerId,
                dto.getUtilityType()
        ).isPresent()) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Connection already exists for this utility"
            );
        }

        boolean pendingExists =
                requestRepo.existsByConsumerIdAndUtilityTypeAndStatusIn(
                        consumerId,
                        dto.getUtilityType(),
                        List.of(ConnectionRequestStatus.PENDING)
                );

        if (pendingExists) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Pending request already exists for this utility"
            );
        }

        ConnectionRequest request = new ConnectionRequest();
        request.setConsumerId(consumerId);
        request.setUtilityType(dto.getUtilityType());
        request.setTariffPlanCode(dto.getTariffPlan());
        request.setStatus(ConnectionRequestStatus.PENDING);
        request.setRequestedAt(LocalDateTime.now());

        requestRepo.save(request);
    }

    public List<ConnectionRequest> getPendingRequests() {
        return requestRepo.findByStatus(ConnectionRequestStatus.PENDING);
    }

    public void approveRequest(
            String requestId,
            ApproveConnectionRequestDto dto,
            String reviewedBy
    ) {

        ConnectionRequest request =
                requestRepo.findByIdAndStatus(
                        requestId,
                        ConnectionRequestStatus.PENDING
                )
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Pending request not found"
                ));

        if (connectionRepo.existsByMeterNumber(dto.getMeterNumber())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Meter already assigned"
            );
        }

        UtilityConnection connection = new UtilityConnection();
        connection.setConsumerId(request.getConsumerId());
        connection.setUtilityType(request.getUtilityType());
        connection.setMeterNumber(dto.getMeterNumber());
        connection.setTariffPlan(request.getTariffPlanCode());
        connection.setActive(true);
        connection.setActivatedAt(LocalDateTime.now());

        connectionRepo.save(connection);

        request.setStatus(ConnectionRequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewedBy);

        requestRepo.save(request);
    }

    public void rejectRequest(String requestId, String reviewedBy) {

        ConnectionRequest request =
                requestRepo.findByIdAndStatus(
                        requestId,
                        ConnectionRequestStatus.PENDING
                )
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Pending request not found"
                ));

        request.setStatus(ConnectionRequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewedBy);

        requestRepo.save(request);
    }

}