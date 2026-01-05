package com.utility.consumer.dto.request;

import com.utility.consumer.enums.UtilityType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateConnectionRequestDto {

    @NotNull
    private UtilityType utilityType;

    @NotBlank
    private String tariffPlan;
}