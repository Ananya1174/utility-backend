package com.utility.common.dto.event;

import lombok.Data;

@Data
public class ConsumerApprovedEvent {
	 private String id;         
	    private String fullName;
	    private String email;
	    private String mobileNumber;
	    private String address;
}