package com.utility.meter.repository;

import com.utility.meter.model.MeterReading;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MeterReadingRepository extends MongoRepository<MeterReading, String> {

    Optional<MeterReading> findTopByConnectionIdOrderByReadingYearDescReadingMonthDesc(
            String connectionId
    );

    boolean existsByConnectionIdAndReadingMonthAndReadingYear(
            String connectionId,
            int readingMonth,
            int readingYear
    );

    List<MeterReading> findByConsumerId(String consumerId);

    Optional<MeterReading> findByConnectionIdAndReadingMonthAndReadingYear(
            String connectionId,
            int readingMonth,
            int readingYear
    );
    List<MeterReading>
    findByReadingMonthAndReadingYear(int readingMonth, int readingYear);
    List<MeterReading> findByConnectionId(String connectionId);
}