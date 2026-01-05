package com.utility.common.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountApprovedEvent {

    private String email;
    private String username;
    private String temporaryPassword;
    private String role;
}