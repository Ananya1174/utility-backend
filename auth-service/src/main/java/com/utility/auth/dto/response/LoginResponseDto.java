package com.utility.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {

    private String accessToken;
    private String tokenType;
    private String role;
    private boolean passwordChangeRequired;
}