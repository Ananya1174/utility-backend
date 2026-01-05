package com.utility.meter.service;

import com.utility.meter.dto.ConnectionDto;
import com.utility.meter.dto.CreateMeterReadingRequest;
import com.utility.meter.dto.MeterReadingResponse;
import com.utility.meter.model.UtilityType;
import com.utility.meter.exception.ApiException;
import com.utility.meter.model.MeterReading;
import com.utility.meter.repository.MeterReadingRepository;
import com.utility.meter.feign.ConsumerConnectionClient;

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
class MeterReadingServiceTest {

    @Mock
    private MeterReadingRepository repository;

    @Mock
    private ConsumerConnectionClient consumerConnectionClient;

    @InjectMocks
    private MeterReadingService service;

    private CreateMeterReadingRequest request() {
        CreateMeterReadingRequest r = new CreateMeterReadingRequest();
        r.setConsumerId("C1");
        r.setConnectionId("CON1");
        r.setUtilityType(UtilityType.ELECTRICITY);
        r.setMeterNumber("MTR1");
        r.setCurrentReading(100);
        r.setReadingMonth(1);
        r.setReadingYear(2025);
        return r;
    }

    private ConnectionDto connection() {
        ConnectionDto c = new ConnectionDto();
        c.setConsumerId("C1");
        c.setUtilityType(UtilityType.ELECTRICITY);
        c.setMeterNumber("MTR1");
        return c;
    }

    @Test
    void addReading_success() {

        when(consumerConnectionClient.getConnectionById("CON1"))
                .thenReturn(connection());

        when(repository.existsByConnectionIdAndReadingMonthAndReadingYear(
                "CON1", 1, 2025))
                .thenReturn(false);

        when(repository.findTopByConnectionIdOrderByReadingYearDescReadingMonthDesc("CON1"))
                .thenReturn(Optional.empty());

        MeterReadingResponse response =
                service.addReading(request());

        assertNotNull(response);
    }

    @Test
    void addReading_duplicateMonth() {

        when(consumerConnectionClient.getConnectionById("CON1"))
                .thenReturn(connection());

        when(repository.existsByConnectionIdAndReadingMonthAndReadingYear(
                "CON1", 1, 2025))
                .thenReturn(true);

        assertThrows(ApiException.class,
                () -> service.addReading(request()));
    }

    @Test
    void getLatest_success() {

        MeterReading r = new MeterReading();
        r.setConnectionId("CON1");

        when(repository.findTopByConnectionIdOrderByReadingYearDescReadingMonthDesc("CON1"))
                .thenReturn(Optional.of(r));

        assertNotNull(service.getLatest("CON1"));
    }

    @Test
    void getLatest_notFound() {

        when(repository.findTopByConnectionIdOrderByReadingYearDescReadingMonthDesc("CON1"))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class,
                () -> service.getLatest("CON1"));
    }

    @Test
    void getByConnection_success() {

        when(repository.findByConnectionId("CON1"))
                .thenReturn(List.of(new MeterReading()));

        assertEquals(1, service.getByConnection("CON1").size());
    }
    @Test
    void getByConsumer_withData() {

        MeterReading reading = new MeterReading();
        reading.setConsumerId("C1");
        reading.setConnectionId("CON1");
        reading.setUtilityType(UtilityType.ELECTRICITY);
        reading.setMeterNumber("MTR1");

        when(repository.findByConsumerId("C1"))
                .thenReturn(List.of(reading));

        List<MeterReadingResponse> result =
                service.getByConsumer("C1");

        assertEquals(1, result.size());   // 🔑 forces map() execution
    }
    @Test
    void getByMonth_withData() {

        MeterReading reading = new MeterReading();
        reading.setReadingMonth(1);
        reading.setReadingYear(2025);
        reading.setUtilityType(UtilityType.ELECTRICITY);
        reading.setMeterNumber("MTR1");

        when(repository.findByReadingMonthAndReadingYear(1, 2025))
                .thenReturn(List.of(reading));

        List<MeterReadingResponse> result =
                service.getByMonth(1, 2025);

        assertEquals(1, result.size());   // 🔑 forces map() execution
    }

    @Test
    void getByConnection_notFound() {

        when(repository.findByConnectionId("CON1"))
                .thenReturn(List.of());

        assertThrows(ApiException.class,
                () -> service.getByConnection("CON1"));
    }
}