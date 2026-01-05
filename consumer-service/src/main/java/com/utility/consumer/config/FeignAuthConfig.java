package com.utility.consumer.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

@Configuration
public class FeignAuthConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication auth =
                    SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.getCredentials() != null) {
                String token = auth.getCredentials().toString();

                requestTemplate.header(
                    "Authorization",
                    "Bearer " + token
                );
            }
        };
    }
}