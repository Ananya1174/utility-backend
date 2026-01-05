package com.utility.common.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class ConsumerApprovedEvent {
	 private String id;          // userId
	    private String fullName;
	    private String email;
	    private String mobileNumber;
	    private String address;
}