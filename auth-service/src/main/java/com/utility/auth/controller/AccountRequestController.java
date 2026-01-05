package com.utility.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utility.auth.dto.request.AccountRequestDto;
import com.utility.auth.dto.request.AccountRequestReviewDto;
import com.utility.auth.dto.response.AccountRequestResponseDto;
import com.utility.auth.model.AccountRequest;
import com.utility.auth.service.AccountRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/account-requests")
@RequiredArgsConstructor
public class AccountRequestController {

    private final AccountRequestService accountRequestService;

  
    @PostMapping
    public ResponseEntity<AccountRequestResponseDto> createRequest(
            @Valid @RequestBody AccountRequestDto dto) {

        AccountRequest request =
                accountRequestService.createAccountRequest(dto);

        AccountRequestResponseDto response =
                new AccountRequestResponseDto(
                        request.getRequestId(),
                        request.getName(),
                        request.getEmail(),
                        request.getPhone(),
                        request.getAddress(),
                        request.getStatus(),
                        request.getCreatedAt()
                );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

  
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<AccountRequestResponseDto>> getPendingRequests() {

        List<AccountRequestResponseDto> responses =
                accountRequestService.getPendingRequests()
                        .stream()
                        .map(r -> new AccountRequestResponseDto(
                                r.getRequestId(),
                                r.getName(),
                                r.getEmail(),
                                r.getPhone(),
                                r.getAddress(),
                                r.getStatus(),
                                r.getCreatedAt()
                        ))
                        .toList();

        return ResponseEntity.ok(responses);
    }

    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/review")
    public ResponseEntity<String> reviewRequest(
            @Valid @RequestBody AccountRequestReviewDto dto) {

        accountRequestService.reviewAccountRequest(dto);

        return ResponseEntity.ok("Account request reviewed successfully");
    }

}