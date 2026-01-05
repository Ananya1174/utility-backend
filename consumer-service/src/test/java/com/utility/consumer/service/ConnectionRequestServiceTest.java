package com.utility.consumer.service;

import com.utility.consumer.dto.request.ApproveConnectionRequestDto;
import com.utility.consumer.dto.request.CreateConnectionRequestDto;
import com.utility.consumer.enums.ConnectionRequestStatus;
import com.utility.consumer.enums.UtilityType;
import com.utility.consumer.exception.ApiException;
import com.utility.consumer.model.ConnectionRequest;
import com.utility.consumer.model.UtilityConnection;
import com.utility.consumer.repository.ConnectionRepository;
import com.utility.consumer.repository.ConnectionRequestRepository;
import com.utility.consumer.repository.ConsumerRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionRequestServiceTest {

    @Mock
    private ConnectionRequestRepository requestRepo;

    @Mock
    private ConnectionRepository connectionRepo;

    @Mock
    private ConsumerRepository consumerRepo;

    @InjectMocks
    private ConnectionRequestService service;

    @Test
    void createRequest_success() {

        CreateConnectionRequestDto dto = new CreateConnectionRequestDto();
        dto.setUtilityType(UtilityType.ELECTRICITY);
        dto.setTariffPlan("DOMESTIC");

        when(consumerRepo.existsById("C1")).thenReturn(true);
        when(connectionRepo.findByConsumerIdAndUtilityType("C1", UtilityType.ELECTRICITY))
                .thenReturn(Optional.empty());
        when(requestRepo.existsByConsumerIdAndUtilityTypeAndStatusIn(
                eq("C1"), eq(UtilityType.ELECTRICITY), any()))
                .thenReturn(false);

        service.createRequest("C1", dto);

        verify(requestRepo).save(any(ConnectionRequest.class));
    }

    @Test
    void createRequest_consumerNotFound() {

        when(consumerRepo.existsById("C1")).thenReturn(false);
        CreateConnectionRequestDto cc=new CreateConnectionRequestDto();

        assertThrows(ApiException.class,
                () -> service.createRequest("C1", cc));
    }
    @Test
    void approveRequest_notFound_lambdaCovered() {

        when(requestRepo.findByIdAndStatus("R404", ConnectionRequestStatus.PENDING))
                .thenReturn(Optional.empty());

        ApproveConnectionRequestDto dto = new ApproveConnectionRequestDto();
        dto.setMeterNumber("MTR1");

        ApiException ex = assertThrows(ApiException.class,
                () -> service.approveRequest("R404", dto, "ADMIN"));

        assertEquals("Pending request not found", ex.getMessage());    }



    @Test
    void rejectRequest_notFound_lambdaCovered() {

        when(requestRepo.findByIdAndStatus("R404", ConnectionRequestStatus.PENDING))
                .thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> service.rejectRequest("R404", "ADMIN"));

        assertEquals("Pending request not found", ex.getMessage());
        }
    @Test
    void approveRequest_success() {

        ConnectionRequest request = new ConnectionRequest();
        request.setConsumerId("C1");
        request.setUtilityType(UtilityType.ELECTRICITY);
        request.setTariffPlanCode("DOMESTIC");

        when(requestRepo.findByIdAndStatus("R1", ConnectionRequestStatus.PENDING))
                .thenReturn(Optional.of(request));

        when(connectionRepo.existsByMeterNumber("MTR1"))
                .thenReturn(false);

        ApproveConnectionRequestDto dto = new ApproveConnectionRequestDto();
        dto.setMeterNumber("MTR1");

        service.approveRequest("R1", dto, "ADMIN");

        verify(connectionRepo).save(any(UtilityConnection.class));
        verify(requestRepo).save(request);
    }
    @Test
    void getPendingRequests_withData() {

        when(requestRepo.findByStatus(ConnectionRequestStatus.PENDING))
                .thenReturn(List.of(new ConnectionRequest()));

        List<ConnectionRequest> result =
                service.getPendingRequests();

        assertEquals(1, result.size());
    }
    @Test
    void rejectRequest_executesLambda() {

        ConnectionRequest request = new ConnectionRequest();
        request.setStatus(ConnectionRequestStatus.PENDING);

        when(requestRepo.findByIdAndStatus("R1", ConnectionRequestStatus.PENDING))
                .thenReturn(Optional.of(request));

        service.rejectRequest("R1", "ADMIN");

        verify(requestRepo).save(request);
    }
    @Test
    void approveRequest_executesLambda() {

        ConnectionRequest request = new ConnectionRequest();
        request.setConsumerId("C1");
        request.setUtilityType(UtilityType.ELECTRICITY);
        request.setTariffPlanCode("DOMESTIC");

        when(requestRepo.findByIdAndStatus("R1", ConnectionRequestStatus.PENDING))
                .thenReturn(Optional.of(request));

        when(connectionRepo.existsByMeterNumber("MTR1"))
                .thenReturn(false);

        ApproveConnectionRequestDto dto = new ApproveConnectionRequestDto();
        dto.setMeterNumber("MTR1");

        service.approveRequest("R1", dto, "ADMIN");

        verify(connectionRepo).save(any());
    }

    @Test
    void rejectRequest_success() {

        ConnectionRequest request = new ConnectionRequest();

        when(requestRepo.findByIdAndStatus("R1", ConnectionRequestStatus.PENDING))
                .thenReturn(Optional.of(request));

        service.rejectRequest("R1", "ADMIN");

        verify(requestRepo).save(request);
    }
}