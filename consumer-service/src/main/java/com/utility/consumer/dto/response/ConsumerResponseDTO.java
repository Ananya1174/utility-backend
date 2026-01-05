package com.utility.consumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConsumerResponseDTO {
    private String id;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String address;
    private boolean active;
}
