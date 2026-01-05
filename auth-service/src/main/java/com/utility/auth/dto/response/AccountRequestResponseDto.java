package com.utility.auth.dto.response;

import java.time.LocalDateTime;

import com.utility.auth.model.AccountRequestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountRequestResponseDto {

    private String requestId;
    private String name;
    private String email;
    private String phone;
    private String address;

    private AccountRequestStatus status;
    private LocalDateTime createdAt;
}