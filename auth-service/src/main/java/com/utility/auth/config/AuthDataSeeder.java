package com.utility.auth.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.utility.auth.model.*;
import com.utility.auth.repository.AccountRequestRepository;
import com.utility.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AuthDataSeeder {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AccountRequestRepository accountRequestRepository;

    @Bean
    CommandLineRunner seedAuthData() {

        return args -> {

            if (userRepository.count() > 0) {
                System.out.println("ℹ️ Auth data already exists. Skipping seed.");
                return;
            }

            System.out.println("🌱 Seeding Auth Service data...");

            List<User> users = new ArrayList<>();
            List<AccountRequest> requests = new ArrayList<>();

            // ---------------- ADMIN USERS ----------------
            users.add(buildUser(
                    "admin",
                    "admin@ubs.com",
                    "Admin@123",
                    Role.ADMIN,
                    false
            ));

            users.add(buildUser(
                    "billing",
                    "billing@ubs.com",
                    "Billing@123",
                    Role.BILLING_OFFICER,
                    false
            ));

            users.add(buildUser(
                    "accounts",
                    "accounts@ubs.com",
                    "Accounts@123",
                    Role.ACCOUNTS_OFFICER,
                    false
            ));

            // ---------------- CONSUMERS ----------------
            for (int i = 1; i <= 10; i++) {

                String email = "consumer" + i + "@mail.com";

                // USER ENTRY
                users.add(
                        User.builder()
                                .username("consumer" + i)
                                .email(email)
                                .password(passwordEncoder.encode("Consumer@123"))
                                .role(Role.CONSUMER)
                                .active(true)
                                .createdAt(LocalDateTime.now().minusDays(30))
                                .passwordChangeRequired(true)
                                .build()
                );

                // ACCOUNT REQUEST (APPROVED)
                requests.add(
                        AccountRequest.builder()
                                .name("Consumer " + i)
                                .email(email)
                                .phone("90000000" + i)
                                .address("City Sector " + i)
                                .status(AccountRequestStatus.APPROVED)
                                .createdAt(LocalDateTime.now().minusDays(35))
                                .reviewedAt(LocalDateTime.now().minusDays(30))
                                .reviewedBy("system-seeder")
                                .build()
                );
            }

            userRepository.saveAll(users);
            accountRequestRepository.saveAll(requests);

            System.out.println("✅ Auth users & approved account requests seeded");
        };
    }

    private User buildUser(
            String username,
            String email,
            String rawPassword,
            Role role,
            boolean passwordChangeRequired
    ) {
        return User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .active(true)
                .createdAt(LocalDateTime.now())
                .passwordChangeRequired(passwordChangeRequired)
                .build();
    }
}