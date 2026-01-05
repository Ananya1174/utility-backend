package com.utility.consumer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.utility.consumer.enums.ConnectionRequestStatus;
import com.utility.consumer.enums.UtilityType;
import com.utility.consumer.model.ConnectionRequest;

public interface ConnectionRequestRepository
        extends MongoRepository<ConnectionRequest, String> {

    List<ConnectionRequest> findByStatus(ConnectionRequestStatus status);

    boolean existsByConsumerIdAndUtilityTypeAndStatusIn(
            String consumerId,
            UtilityType utilityType,
            List<ConnectionRequestStatus> statuses
    );

    Optional<ConnectionRequest> findByIdAndStatus(
            String id,
            ConnectionRequestStatus status
    );
    List<ConnectionRequest> findByConsumerId(String consumerId);
}