package com.utility.payment.config;

import com.utility.payment.security.JwtAuthenticationFilter;
import com.utility.payment.security.JwtUtil;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                /* ================= CONSUMER PAYMENTS ================= */
                .requestMatchers(
                        HttpMethod.POST,
                        "/payments/online/initiate",
                        "/payments/online/confirm"
                ).hasRole("CONSUMER")

                /* ================= OFFLINE PAYMENTS ================= */
                .requestMatchers(
                        HttpMethod.POST,
                        "/payments/offline"
                ).hasRole("ACCOUNTS_OFFICER")

                /* ================= PAYMENT VIEW ================= */
                .requestMatchers(
                        HttpMethod.GET,
                        "/payments",
                        "/payments/bill/**",
                        "/payments/consumer/**",
                        "/payments/outstanding/**",
                        "/payments/invoice/**"
                ).hasAnyRole("CONSUMER", "ACCOUNTS_OFFICER", "ADMIN","BILLING_OFFICER")

                /* ================= PAYMENT DASHBOARD (CARDS) ================= */
                .requestMatchers(
                        HttpMethod.GET,
                        "/dashboard/payments/revenue-summary",
                        "/dashboard/payments/outstanding-summary",
                        "/dashboard/payments/outstanding-monthly"
                ).hasAnyRole("ADMIN", "ACCOUNTS_OFFICER","BILLING_OFFICER")

                /* ================= PAYMENT REPORTS / ANALYTICS ================= */
                .requestMatchers(
                        HttpMethod.GET,
                        "/dashboard/payments/failed-summary",
                        "/dashboard/payments/revenue-by-mode",
                        "/dashboard/payments/consumer-summary",
                        "/dashboard/payments/revenue-yearly"
                ).hasAnyRole("ADMIN","ACCOUNTS_OFFICER")

                /* ================= FALLBACK ================= */
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}