package com.utility.payment.dto;

import lombok.Data;

@Data
public class ConsumerResponse {

    private String id;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String address;
    private boolean active;
}