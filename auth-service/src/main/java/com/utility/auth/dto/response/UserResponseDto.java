package com.utility.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private String userId;
    private String username;
    private String email;
    private String role;
    private String status;
}