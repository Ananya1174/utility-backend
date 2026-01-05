package com.utility.consumer.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.utility.consumer.model.Consumer;

public interface ConsumerRepository extends MongoRepository<Consumer, String> {
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    Optional<Consumer> findByEmail(String email);
    boolean existsById(String id);


}
