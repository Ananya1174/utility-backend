package com.utility.consumer.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "consumers")
@Data
public class Consumer {

    @Id
    private String id;

    @NotBlank
    private String fullName;

    @Email
    private String email;

    @NotBlank
    private String mobileNumber;

    @NotBlank
    private String address;

    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
