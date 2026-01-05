package com.utility.payment.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


import com.utility.payment.dto.BillResponse;
import com.utility.payment.model.BillStatus;

@FeignClient(
    name = "billing-service",
    configuration = FeignSecurityConfig.class
)
public interface BillingClient {

    @PutMapping("/bills/{billId}/mark-paid")
    void markPaid(@PathVariable("billId") String billId);

    @GetMapping("/bills/{billId}")
    BillResponse getBill(@PathVariable("billId") String billId);

    // ✅ ADD THIS (matches controller exactly)
    @GetMapping("/bills")
    List<BillResponse> getAllBills(
        @RequestParam(value = "status", required = false) BillStatus status,
        @RequestParam(value = "month", required = false) Integer month,
        @RequestParam(value = "year", required = false) Integer year,
        @RequestParam(value = "consumerId", required = false) String consumerId
    );
    @GetMapping("/dashboard/billing/total-billed")
    double getTotalBilled();
    @GetMapping("/dashboard/billing/total-billed-monthly")
    double getTotalBilledForMonth(
    		@RequestParam("month") int month,
            @RequestParam("year") int year
    );
}