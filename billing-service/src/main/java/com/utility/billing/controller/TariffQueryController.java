package com.utility.billing.controller;

import com.utility.billing.dto.TariffResponseDto;
import com.utility.billing.model.UtilityType;
import com.utility.billing.service.TariffQueryService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tariffs")
@RequiredArgsConstructor
public class TariffQueryController {

    private final TariffQueryService tariffQueryService;

    @GetMapping
    public TariffResponseDto getTariffsByUtility(
    		@RequestParam(name = "utilityType") UtilityType utilityType
    ) {
        return tariffQueryService.getTariffsByUtility(utilityType);
    }
}