package com.utility.consumer.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.utility.consumer.enums.ConnectionRequestStatus;
import com.utility.consumer.enums.UtilityType;
import com.utility.consumer.model.ConnectionRequest;
import com.utility.consumer.model.Consumer;
import com.utility.consumer.model.UtilityConnection;
import com.utility.consumer.repository.ConnectionRepository;
import com.utility.consumer.repository.ConnectionRequestRepository;
import com.utility.consumer.repository.ConsumerRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ConsumerDataSeeder {

    private final ConsumerRepository consumerRepo;
    private final ConnectionRepository connectionRepo;
    private final ConnectionRequestRepository requestRepo;

    @Bean
    CommandLineRunner seedConsumers() {

        return args -> {

            if (consumerRepo.count() > 0) {
                return;
            }
            // ---------------- CONSUMERS ----------------
            for (int i = 1; i <= 10; i++) {

                Consumer consumer = new Consumer();
                consumer.setFullName("Consumer " + i);
                consumer.setEmail("consumer" + i + "@mail.com");
                consumer.setMobileNumber("90000000" + i);
                consumer.setAddress("City Sector " + i);
                consumer.setActive(true);
                consumer.setCreatedAt(LocalDateTime.now().minusDays(30));

                consumer = consumerRepo.save(consumer);

                seedConnections(consumer);
                seedRequests(consumer);
            }

        };
    }

    private void seedConnections(Consumer consumer) {

        List<UtilityConnection> connections = List.of(
            buildConnection(consumer.getId(), UtilityType.ELECTRICITY, "DOMESTIC"),
            buildConnection(consumer.getId(), UtilityType.WATER, "RESIDENTIAL"),
            buildConnection(consumer.getId(), UtilityType.GAS, "PNG_DOMESTIC"),
            buildConnection(consumer.getId(), UtilityType.INTERNET, "BASIC_50MBPS")
        );

        connectionRepo.saveAll(connections);
    }

    private UtilityConnection buildConnection(
            String consumerId,
            UtilityType utility,
            String plan) {

        UtilityConnection c = new UtilityConnection();
        c.setConsumerId(consumerId);
        c.setUtilityType(utility);
        c.setTariffPlan(plan);
        c.setMeterNumber("MTR-" + UUID.randomUUID().toString().substring(0, 6));
        c.setActive(true);
        c.setActivatedAt(LocalDateTime.now().minusDays(10));

        return c;
    }

    private void seedRequests(Consumer consumer) {

        // PENDING
        requestRepo.save(buildRequest(
                consumer.getId(),
                UtilityType.ELECTRICITY,
                "COMMERCIAL",
                ConnectionRequestStatus.PENDING
        ));

        // APPROVED
        ConnectionRequest approved = buildRequest(
                consumer.getId(),
                UtilityType.WATER,
                "BULK",
                ConnectionRequestStatus.APPROVED
        );
        approved.setReviewedAt(LocalDateTime.now().minusDays(5));
        approved.setReviewedBy("admin");
        requestRepo.save(approved);

        // REJECTED
        ConnectionRequest rejected = buildRequest(
                consumer.getId(),
                UtilityType.INTERNET,
                "PREMIUM_100MBPS",
                ConnectionRequestStatus.REJECTED
        );
        rejected.setReviewedAt(LocalDateTime.now().minusDays(3));
        rejected.setReviewedBy("admin");
        rejected.setRejectionReason("Documents incomplete");
        requestRepo.save(rejected);
    }

    private ConnectionRequest buildRequest(
            String consumerId,
            UtilityType utility,
            String plan,
            ConnectionRequestStatus status) {

        ConnectionRequest r = new ConnectionRequest();
        r.setConsumerId(consumerId);
        r.setUtilityType(utility);
        r.setTariffPlanCode(plan);
        r.setStatus(status);
        r.setRequestedAt(LocalDateTime.now().minusDays(7));

        return r;
    }
}