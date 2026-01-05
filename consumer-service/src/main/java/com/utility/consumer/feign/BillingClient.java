package com.utility.consumer.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "billing-service")
public interface BillingClient {

    @GetMapping("/bills/consumer/{consumerId}")
    List<BillResponseDto> getBillsByConsumer(@PathVariable String consumerId);
}