package com.utility.billing.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
	    name = "meter-reading-service",
	    path = "/meter-readings",
	    fallback = MeterReadingFallback.class
	)
	public interface MeterReadingClient {

	    @GetMapping("/connection/{connectionId}")
	    List<MeterReadingResponse> getByConnection(
	    		@PathVariable("connectionId") String connectionId
	    );

	    @GetMapping("/latest/{connectionId}")
	    MeterReadingResponse getLatest(
	    		@PathVariable("connectionId") String connectionId
	    );
	}
