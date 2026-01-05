package com.utility.billing.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
	    name = "consumer-service",
	    contextId = "consumerClient",
	    path = "/consumers"
	)
	public interface ConsumerClient {

	    @GetMapping("/{id}")
	    ConsumerResponse getConsumerById(
	    		@PathVariable("id") String id
	    );
	}