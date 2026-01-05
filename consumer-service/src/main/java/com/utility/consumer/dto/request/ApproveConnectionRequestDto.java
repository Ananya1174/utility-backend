package com.utility.consumer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApproveConnectionRequestDto {

    @NotBlank
    private String meterNumber;
}