package com.utility.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountRequestReviewDto {

    @NotBlank(message = "Request ID is required")
    private String requestId;

    @NotBlank(message = "Decision is required (APPROVE / REJECT)")
    private String decision; 
}