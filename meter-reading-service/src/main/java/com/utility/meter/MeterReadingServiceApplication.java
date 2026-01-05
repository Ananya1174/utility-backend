package com.utility.meter;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
public class MeterReadingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeterReadingServiceApplication.class, args);
	}

}
