package com.utility.common.dto.event;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor   
public class AccountRejectedEvent {
    private String email;
}