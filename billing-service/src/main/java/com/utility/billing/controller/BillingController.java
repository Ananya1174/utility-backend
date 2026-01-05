package com.utility.billing.controller;

import com.utility.billing.dto.BillResponse;
import com.utility.billing.dto.GenerateBillRequest;
import com.utility.billing.model.BillStatus;
import com.utility.billing.service.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService service;

    @PostMapping
    public ResponseEntity<BillResponse> generate(@Valid @RequestBody GenerateBillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generateBill(request));
    }

    @GetMapping("/consumer/{consumerId}")
    public List<BillResponse> byConsumer(@PathVariable("consumerId") String consumerId) {
        return service.getBillsByConsumer(consumerId);
    }
    @PutMapping("/{billId}/mark-paid")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markBillAsPaid(@PathVariable("billId") String billId) {
        service.markBillAsPaid(billId);
    }
    @GetMapping("/{billId}")
    public BillResponse getBillById(@PathVariable("billId") String billId) {
        return service.getBillById(billId);
    }
    @GetMapping
    public List<BillResponse> getAllBills(
            @RequestParam(name = "status", required = false) BillStatus status,
            @RequestParam(name = "month", required = false) Integer month,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "consumerId", required = false) String consumerId
    ) {
        return service.getAllBills(status, month, year, consumerId);
    }
    
}