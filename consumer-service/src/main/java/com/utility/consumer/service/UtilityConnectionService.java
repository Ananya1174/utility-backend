package com.utility.consumer.service;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.utility.consumer.dto.response.ConnectionResponseDto;
import com.utility.consumer.enums.ConnectionRequestStatus;
import com.utility.consumer.exception.ApiException;
import com.utility.consumer.model.ConnectionRequest;
import com.utility.consumer.model.Consumer;
import com.utility.consumer.model.UtilityConnection;
import com.utility.consumer.repository.ConnectionRepository;
import com.utility.consumer.repository.ConnectionRequestRepository;
import com.utility.consumer.repository.ConsumerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UtilityConnectionService {

    private final ConnectionRepository connectionRepository;
    private final ConsumerRepository consumerRepository;
    private final ConnectionRequestRepository requestRepository;

    public List<ConnectionResponseDto> getConnectionsByUserId(String userId) {

    	Consumer consumer = consumerRepository.findById(userId)
                .orElseThrow(() ->
                        new ApiException(HttpStatus.NOT_FOUND, "Consumer not found"));

        // 1️⃣ Approved connections
        List<ConnectionResponseDto> activeConnections =
                connectionRepository.findByConsumerId(consumer.getId())
                        .stream()
                        .map(this::mapActiveConnection)
                        .toList();

        // 2️⃣ Pending / rejected requests
        List<ConnectionResponseDto> requests =
                requestRepository.findByConsumerId(consumer.getId())
                        .stream()
                        .map(this::mapRequestConnection)
                        .toList();


        // 3️⃣ Merge both
        return Stream.concat(activeConnections.stream(), requests.stream())
                .toList();
    }
    private ConnectionResponseDto mapActiveConnection(UtilityConnection c) {

        ConnectionResponseDto dto = new ConnectionResponseDto();
        dto.setId(c.getId());
        dto.setConsumerId(c.getConsumerId());
        dto.setUtilityType(c.getUtilityType());
        dto.setTariffPlan(c.getTariffPlan());
        dto.setMeterNumber(c.getMeterNumber());
        dto.setStatus(ConnectionRequestStatus.APPROVED);
        dto.setActivatedAt(c.getActivatedAt());

        return dto;
    }
    private ConnectionResponseDto mapRequestConnection(ConnectionRequest r) {

        ConnectionResponseDto dto = new ConnectionResponseDto();
        dto.setId(r.getId());
        dto.setConsumerId(r.getConsumerId());
        dto.setUtilityType(r.getUtilityType());
        dto.setTariffPlan(r.getTariffPlanCode());
        dto.setStatus(r.getStatus());
        dto.setRequestedAt(r.getRequestedAt());

        return dto;
    }
    public List<ConnectionResponseDto> getAllConnections() {

        return connectionRepository.findAll()
                .stream()
                .filter(UtilityConnection::isActive)
                .map(this::mapToDto)
                .toList();
    }
    private ConnectionResponseDto mapToDto(UtilityConnection connection) {

        ConnectionResponseDto dto = new ConnectionResponseDto();
        dto.setId(connection.getId());
        dto.setConsumerId(connection.getConsumerId());
        dto.setUtilityType(connection.getUtilityType());
        dto.setMeterNumber(connection.getMeterNumber());
        dto.setTariffPlan(connection.getTariffPlan());
        dto.setActive(connection.isActive());
        dto.setActivatedAt(connection.getActivatedAt());

        return dto;
    }
    public ConnectionResponseDto getConnectionById(String connectionId) {

        UtilityConnection connection =
                connectionRepository.findById(connectionId)
                        .orElseThrow(() ->
                                new ApiException(
                                        HttpStatus.NOT_FOUND,
                                        "Connection not found"
                                )
                        );
   

        if (!connection.isActive()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Connection is inactive"
            );
        }

        return mapToDto(connection);    }
}