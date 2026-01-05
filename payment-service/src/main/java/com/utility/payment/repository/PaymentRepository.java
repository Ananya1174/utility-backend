package com.utility.payment.repository;

import com.utility.payment.model.Payment;
import com.utility.payment.model.PaymentStatus;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository
extends MongoRepository<Payment, String> {

boolean existsByBillIdAndStatus(
    String billId,
    PaymentStatus status
);
List<Payment> findByBillId(String billId);
List<Payment> findByConsumerIdOrderByCreatedAtDesc(String consumerId);
List<Payment> findByStatus(PaymentStatus status);

List<Payment> findByBillingMonthAndBillingYear(
        int billingMonth,
        int billingYear
);

List<Payment> findByBillingYear(int billingYear);
boolean existsByBillId(String billId);
List<Payment> findByBillingMonthAndBillingYearAndStatus(
        int billingMonth,
        int billingYear,
        PaymentStatus status
);
Optional<Payment> findByBillIdAndStatus(
	    String billId,
	    PaymentStatus status
	);
List<Payment> findAllByOrderByCreatedAtDesc();
}