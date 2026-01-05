package com.utility.common.dto.event;

import lombok.Data;

@Data
public class PasswordResetEvent {

    private String email;
    private String resetToken;
}