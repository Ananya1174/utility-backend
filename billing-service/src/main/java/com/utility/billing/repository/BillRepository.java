package com.utility.billing.repository;

import com.utility.billing.model.Bill;
import com.utility.billing.model.BillStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends MongoRepository<Bill, String> {

	List<Bill> findByConsumerId(String consumerId);

	Optional<Bill> findByConnectionIdAndBillingMonthAndBillingYearAndStatusNot(String connectionId, int billingMonth,
			int billingYear, BillStatus status);

	Optional<Bill> findTopByConnectionIdOrderByBillingYearDescBillingMonthDesc(String connectionId);

	List<Bill> findByStatus(BillStatus status);

	List<Bill> findByStatusAndDueDateBefore(BillStatus status, LocalDate date);

	List<Bill> findByStatusAndDueDateBetween(BillStatus status, LocalDate start, LocalDate end);

	long countByBillingMonthAndBillingYear(int month, int year);

	long countByBillingMonthAndBillingYearAndStatus(int month, int year, BillStatus status);

	List<Bill> findByBillingMonthAndBillingYear(int month, int year);

	List<Bill> findByConsumerIdAndBillingMonthAndBillingYear(String consumerId, int month, int year);
	List<Bill> findByConsumerIdAndStatus(String consumerId, BillStatus status);
	boolean existsByConnectionIdAndBillingMonthAndBillingYear(
	        String connectionId,
	        int billingMonth,
	        int billingYear
	);
}