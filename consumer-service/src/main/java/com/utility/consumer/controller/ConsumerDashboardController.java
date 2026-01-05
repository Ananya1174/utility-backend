package com.utility.consumer.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utility.consumer.dto.dashboard.ConsumerDashboardSummaryDto;
import com.utility.consumer.service.ConsumerDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/consumers/dashboard")
@RequiredArgsConstructor
public class ConsumerDashboardController {

    private final ConsumerDashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('CONSUMER')")
    public ConsumerDashboardSummaryDto summary() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String consumerId = auth.getName();

        return dashboardService.getDashboardSummary(consumerId);
    }
}