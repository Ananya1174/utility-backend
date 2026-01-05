package com.utility.billing.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Configuration
public class FeignAuthInterceptor {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {

                Authentication authentication =
                        SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof UsernamePasswordAuthenticationToken auth) {

                    Object details = auth.getDetails();

                    if (details instanceof String token) {
                        template.header("Authorization", "Bearer " + token);
                    }
                }
            }
        };
    }
}