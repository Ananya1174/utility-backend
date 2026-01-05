package com.utility.common.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountRejectedEvent {
    private String email;
}