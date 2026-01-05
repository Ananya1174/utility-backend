package com.utility.consumer.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@FeignClient(name = "payment-service")
public interface PaymentClient {

    @GetMapping("/payments/consumer/{consumerId}")
    List<PaymentResponseDto> getPaymentsByConsumer(@PathVariable String consumerId);
}