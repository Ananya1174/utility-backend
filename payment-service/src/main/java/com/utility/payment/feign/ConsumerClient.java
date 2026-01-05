package com.utility.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.utility.payment.dto.ConsumerResponse;

@FeignClient(
    name = "consumer-service",
    path = "/consumers"
)
public interface ConsumerClient {

	 @GetMapping("/{consumerId}")
	    ConsumerResponse getConsumerById(@PathVariable("consumerId") String consumerId);
}