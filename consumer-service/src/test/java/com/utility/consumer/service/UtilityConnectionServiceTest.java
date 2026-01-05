package com.utility.consumer.service;

import com.utility.consumer.dto.response.ConnectionResponseDto;
import com.utility.consumer.enums.ConnectionRequestStatus;
import com.utility.consumer.enums.UtilityType;
import com.utility.consumer.exception.ApiException;
import com.utility.consumer.model.ConnectionRequest;
import com.utility.consumer.model.Consumer;
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
class UtilityConnectionServiceTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private ConsumerRepository consumerRepository;

    @Mock
    private ConnectionRequestRepository requestRepository;

    @InjectMocks
    private UtilityConnectionService service;

    @Test
    void getConnectionsByUserId_success() {

        Consumer consumer = new Consumer();
        consumer.setId("C1");

        when(consumerRepository.findById("C1"))
                .thenReturn(Optional.of(consumer));

        when(connectionRepository.findByConsumerId("C1"))
                .thenReturn(List.of());

        when(requestRepository.findByConsumerId("C1"))
                .thenReturn(List.of());

        List<ConnectionResponseDto> result =
                service.getConnectionsByUserId("C1");

        assertNotNull(result);
    }
    @Test
    void getConnectionsByUserId_withActiveAndRequest() {

        Consumer consumer = new Consumer();
        consumer.setId("C1");

        UtilityConnection active = new UtilityConnection();
        active.setId("UC1");
        active.setConsumerId("C1");
        active.setUtilityType(UtilityType.ELECTRICITY);
        active.setActive(true);

        ConnectionRequest req = new ConnectionRequest();
        req.setId("R1");
        req.setConsumerId("C1");
        req.setUtilityType(UtilityType.ELECTRICITY);
        req.setStatus(ConnectionRequestStatus.PENDING);

        when(consumerRepository.findById("C1"))
                .thenReturn(Optional.of(consumer));

        when(connectionRepository.findByConsumerId("C1"))
                .thenReturn(List.of(active));

        when(requestRepository.findByConsumerId("C1"))
                .thenReturn(List.of(req));

        List<ConnectionResponseDto> result =
                service.getConnectionsByUserId("C1");

        assertEquals(2, result.size()); // 👈 KEY
    }
    @Test
    void getAllConnections_onlyActive() {

        UtilityConnection active = new UtilityConnection();
        active.setId("UC1");
        active.setActive(true);

        when(connectionRepository.findAll())
                .thenReturn(List.of(active));

        List<ConnectionResponseDto> result =
                service.getAllConnections();

        assertEquals(1, result.size());
    }

    @Test
    void getConnectionById_inactive() {

        UtilityConnection connection = new UtilityConnection();
        connection.setActive(false);

        when(connectionRepository.findById("ID1"))
                .thenReturn(Optional.of(connection));

        assertThrows(ApiException.class,
                () -> service.getConnectionById("ID1"));
    }
}