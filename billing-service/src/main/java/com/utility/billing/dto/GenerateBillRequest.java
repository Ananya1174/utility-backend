package com.utility.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GenerateBillRequest {

    @NotBlank
    private String consumerId;

    @NotBlank
    private String connectionId;

    @Min(1)
    @NotBlank
    private int billingMonth;

    @Min(2020)
    @NotBlank
    private int billingYear;
}