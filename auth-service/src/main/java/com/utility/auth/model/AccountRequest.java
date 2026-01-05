package com.utility.auth.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "account_requests")
public class AccountRequest {

    @Id
    private String requestId;

    private String name;
    private String email;
    private String phone;
    private String address;

    private AccountRequestStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    private String reviewedBy; 
}