package com.utility.consumer.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.utility.consumer.enums.UtilityType;

import lombok.Data;

@Document(collection = "utility_connections")
@Data
public class UtilityConnection {

	@Id
	private String id;

	private String consumerId;

	private UtilityType utilityType;

	private String meterNumber;

	private String tariffPlan;

	private boolean active;

	private LocalDateTime activatedAt;
}