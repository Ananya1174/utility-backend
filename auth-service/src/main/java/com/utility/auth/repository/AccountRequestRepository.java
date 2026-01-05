package com.utility.auth.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.utility.auth.model.AccountRequest;
import com.utility.auth.model.AccountRequestStatus;

public interface AccountRequestRepository
        extends MongoRepository<AccountRequest, String> {

    List<AccountRequest> findByStatus(AccountRequestStatus status);

    boolean existsByEmail(String email);
}