package com.utility.common.dto.event;

import lombok.Builder;
import lombok.NoArgsConstructor;

import lombok.Data;

@Data
@NoArgsConstructor
public class AccountRejectedEvent {
    private String email;
}