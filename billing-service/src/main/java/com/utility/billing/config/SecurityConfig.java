package com.utility.billing.config;

import com.utility.billing.security.JwtAuthenticationFilter;
import com.utility.billing.security.JwtUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                /* ================= TARIFF MANAGEMENT ================= */
                .requestMatchers(HttpMethod.POST, "/tariffs/plans")
                    .hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, "/tariffs/plans/*/deactivate")
                    .hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/tariffs/slabs")
                    .hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET, "/tariffs/plans/active")
                    .hasAnyRole("ADMIN", "BILLING_OFFICER")

                .requestMatchers(HttpMethod.GET, "/tariffs")
                    .hasAnyRole("ADMIN", "BILLING_OFFICER", "CONSUMER")

                /* ================= BILL GENERATION ================= */
                .requestMatchers(HttpMethod.POST, "/bills")
                    .hasRole("BILLING_OFFICER")
                    .requestMatchers(HttpMethod.GET, "/bills")
                    .permitAll()

                /* ================= BILL VIEW ================= */
                .requestMatchers(HttpMethod.GET, "/bills/**")
                    .hasAnyRole("CONSUMER", "ACCOUNTS_OFFICER", "BILLING_OFFICER","ADMIN")
                    .requestMatchers(HttpMethod.GET, "/bills")
                    .hasAnyRole("ADMIN", "BILLING_OFFICER")
                    /* ================= DASHBOARD ================= */

                    .requestMatchers(HttpMethod.GET, "/dashboard/bills-summary")
                        .hasAnyRole("ADMIN","BILLING_OFFICER")

                    .requestMatchers(HttpMethod.GET, "/dashboard/consumption-summary")
                        .hasAnyRole("ADMIN","BILLING_OFFICER")

                    .requestMatchers(HttpMethod.GET, "/dashboard/consumption-average")
                        .hasRole("ADMIN")

                    .requestMatchers(HttpMethod.GET, "/dashboard/consumption/utility")
                        .hasAnyRole("ADMIN","BILLING_OFFICER")

                    .requestMatchers(HttpMethod.GET, "/dashboard/billing/consumer-summary")
                        .hasRole("ADMIN")

                    .requestMatchers(HttpMethod.GET, "/dashboard/billing/consumer/*")
                        .hasAnyRole("ADMIN", "BILLING_OFFICER")

                .anyRequest().authenticated()
            )
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtUtil),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}