package com.utility.meter.feign;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.utility.meter.dto.ConnectionDto;

@FeignClient(name = "consumer-service")
public interface ConsumerConnectionClient {

    @GetMapping("/connections/internal/all")
    List<ConnectionDto> getAllConnections();
    
    @GetMapping("/connections/{connectionId}")
    ConnectionDto getConnectionById(@PathVariable String connectionId);
}