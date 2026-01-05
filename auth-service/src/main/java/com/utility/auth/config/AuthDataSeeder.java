package com.utility.auth.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.utility.auth.model.AccountRequest;
import com.utility.auth.model.AccountRequestStatus;
import com.utility.auth.model.Role;
import com.utility.auth.model.User;
import com.utility.auth.repository.AccountRequestRepository;
import com.utility.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AuthDataSeeder {

    private static final String DEFAULT_PASSWORD = "ChangeMe@123";

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AccountRequestRepository accountRequestRepository;

    @Bean
    CommandLineRunner seedAuthData() {

        return args -> {

            if (userRepository.count() > 0) {
                return;
            }

            String adminPassword =
                    System.getenv().getOrDefault("ADMIN_PASSWORD", DEFAULT_PASSWORD);
            String billingPassword =
                    System.getenv().getOrDefault("BILLING_PASSWORD", DEFAULT_PASSWORD);
            String accountsPassword =
                    System.getenv().getOrDefault("ACCOUNTS_PASSWORD", DEFAULT_PASSWORD);
            String consumerPassword =
                    System.getenv().getOrDefault("CONSUMER_PASSWORD", DEFAULT_PASSWORD);

            List<User> users = new ArrayList<>();
            List<AccountRequest> requests = new ArrayList<>();

            users.add(buildUser("admin", "admin@ubs.com", adminPassword, Role.ADMIN, false));
            users.add(buildUser("billing", "billing@ubs.com", billingPassword, Role.BILLING_OFFICER, false));
            users.add(buildUser("accounts", "accounts@ubs.com", accountsPassword, Role.ACCOUNTS_OFFICER, false));

            for (int i = 1; i <= 10; i++) {

                String email = "consumer" + i + "@mail.com";

                users.add(
                        User.builder()
                                .username("consumer" + i)
                                .email(email)
                                .password(passwordEncoder.encode(consumerPassword))
                                .role(Role.CONSUMER)
                                .active(true)
                                .createdAt(LocalDateTime.now().minusDays(30))
                                .passwordChangeRequired(true)
                                .build()
                );

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