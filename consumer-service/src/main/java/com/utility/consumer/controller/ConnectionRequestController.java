package com.utility.consumer.controller;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.utility.consumer.dto.request.ApproveConnectionRequestDto;
import com.utility.consumer.dto.request.CreateConnectionRequestDto;
import com.utility.consumer.model.ConnectionRequest;
import com.utility.consumer.service.ConnectionRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/connections/requests")
@RequiredArgsConstructor
public class ConnectionRequestController {

    private final ConnectionRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createRequest(
            @RequestHeader("X-Consumer-Id") String consumerId,
            @Valid @RequestBody CreateConnectionRequestDto dto) {

        requestService.createRequest(consumerId, dto);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ConnectionRequest> getPending() {
        return requestService.getPendingRequests();
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public void approve(
    		@PathVariable("id") String id,
            @Valid @RequestBody ApproveConnectionRequestDto dto,
            Principal principal
    ) {
        requestService.approveRequest(id, dto, principal.getName());
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void reject(
    		@PathVariable("id") String id,
            Principal principal
    ) {
        requestService.rejectRequest(id, principal.getName());
    }

}