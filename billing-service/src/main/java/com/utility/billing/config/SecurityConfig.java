package com.utility.billing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.utility.billing.security.JwtAuthenticationFilter;
import com.utility.billing.security.JwtUtil;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String BILLS = "/bills";
    private static final String ADMIN = "ADMIN";
    private static final String BILLING_OFFICER = "BILLING_OFFICER";
    private static final String CONSUMER = "CONSUMER";
    private static final String ACCOUNTS_OFFICER = "ACCOUNTS_OFFICER";

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtUtil jwtUtil
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                HttpMethod.POST,
                                "/tariffs/plans"
                        ).hasRole(ADMIN)

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/tariffs/plans/*/deactivate"
                        ).hasRole(ADMIN)

                        .requestMatchers(
                                HttpMethod.POST,
                                "/tariffs/slabs"
                        ).hasRole(ADMIN)

                        .requestMatchers(
                                HttpMethod.GET,
                                "/tariffs/plans/active"
                        ).hasAnyRole(ADMIN, BILLING_OFFICER)

                        .requestMatchers(
                                HttpMethod.GET,
                                "/tariffs"
                        ).hasAnyRole(ADMIN, BILLING_OFFICER, CONSUMER)

                        .requestMatchers(
                                HttpMethod.POST,
                                BILLS
                        ).hasRole(BILLING_OFFICER)

                        .requestMatchers(
                                HttpMethod.GET,
                                BILLS
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/bills/**"
                        ).hasAnyRole(
                                CONSUMER,
                                ACCOUNTS_OFFICER,
                                BILLING_OFFICER,
                                ADMIN
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/dashboard/bills-summary"
                        ).hasAnyRole(ADMIN, BILLING_OFFICER)

                        .requestMatchers(
                                HttpMethod.GET,
                                "/dashboard/consumption-summary"
                        ).hasAnyRole(ADMIN, BILLING_OFFICER)

                        .requestMatchers(
                                HttpMethod.GET,
                                "/dashboard/consumption-average"
                        ).hasRole(ADMIN)

                        .requestMatchers(
                                HttpMethod.GET,
                                "/dashboard/consumption/utility"
                        ).hasAnyRole(ADMIN, BILLING_OFFICER)

                        .requestMatchers(
                                HttpMethod.GET,
                                "/dashboard/billing/consumer-summary"
                        ).hasRole(ADMIN)

                        .requestMatchers(
                                HttpMethod.GET,
                                "/dashboard/billing/consumer/*"
                        ).hasAnyRole(ADMIN, BILLING_OFFICER)

                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}