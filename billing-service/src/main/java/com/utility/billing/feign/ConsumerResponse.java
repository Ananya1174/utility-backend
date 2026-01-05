package com.utility.billing.feign;

import lombok.Data;

@Data
public class ConsumerResponse {

    private String id;
    private String name;
    private String email;
}