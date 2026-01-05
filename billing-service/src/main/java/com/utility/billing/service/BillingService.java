package com.utility.billing.service;

import com.utility.billing.dto.BillResponse;
import lombok.extern.slf4j.Slf4j;
import com.utility.billing.dto.GenerateBillRequest;
import com.utility.billing.event.BillEventPublisher;
import com.utility.billing.exception.ApiException;
import com.utility.billing.feign.ConnectionClient;
import com.utility.billing.feign.ConsumerClient;
import com.utility.billing.feign.MeterReadingClient;
import com.utility.billing.feign.MeterReadingResponse;
import com.utility.billing.model.Bill;
import com.utility.billing.model.BillStatus;
import com.utility.billing.model.TariffSlab;
import com.utility.billing.repository.BillRepository;
import com.utility.billing.repository.TariffSlabRepository;
import com.utility.common.dto.event.BillGeneratedEvent;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

	private final BillRepository billRepository;
	private final MeterReadingClient meterClient;
	private final TariffSlabRepository slabRepository;
	private final ConsumerClient consumerClient;
	private final BillEventPublisher billEventPublisher;
	private final ConnectionClient connectionClient;

	@CircuitBreaker(name = "meterReadingCB", fallbackMethod = "meterFallback")
	public MeterReadingResponse fetchLatestMeterReading(String connectionId) {
		return meterClient.getLatest(connectionId);
	}

	public MeterReadingResponse meterFallback(String connectionId, Throwable ex) {
		return null;
	}

	public BillResponse generateBill(GenerateBillRequest request) {

		var lastBillOpt = billRepository
				.findTopByConnectionIdOrderByBillingYearDescBillingMonthDesc(request.getConnectionId());
		var reading = fetchLatestMeterReading(request.getConnectionId());

		if (reading == null) {
			throw new ApiException("Meter reading not available for next billing period", HttpStatus.BAD_REQUEST);
		}

		int billingMonth = reading.getReadingMonth();
		int billingYear = reading.getReadingYear();

		if (lastBillOpt.isPresent()) {
			var lastBill = lastBillOpt.get();

			if (billingYear < lastBill.getBillingYear()
					|| (billingYear == lastBill.getBillingYear() && billingMonth <= lastBill.getBillingMonth())) {
				throw new ApiException("Meter reading for next billing period not available yet",
						HttpStatus.BAD_REQUEST);
			}
		}

		billRepository.findByConnectionIdAndBillingMonthAndBillingYearAndStatusNot(request.getConnectionId(),
				billingMonth, billingYear, BillStatus.PAID).ifPresent(b -> {
					throw new ApiException("Unpaid bill already exists for this billing period",
							HttpStatus.BAD_REQUEST);
				});

		long units = reading.getConsumptionUnits();
		if (units <= 0) {
			throw new ApiException("Invalid consumption units", HttpStatus.BAD_REQUEST);
		}

		var connection = connectionClient.getConnectionById(request.getConnectionId());

		if (connection == null || !connection.isActive()) {
			throw new ApiException("Connection not active or not found", HttpStatus.BAD_REQUEST);
		}

		if (!connection.getUtilityType().equals(reading.getUtilityType())) {
			throw new ApiException("Utility type mismatch between meter and connection", HttpStatus.BAD_REQUEST);
		}

		List<TariffSlab> slabs = slabRepository.findByUtilityTypeAndPlanCodeOrderByMinUnitsAsc(
				connection.getUtilityType(), connection.getTariffPlan());
		if (slabs.isEmpty()) {
			throw new ApiException("Tariff slabs not configured", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		double energyCharge = calculateFromSlabs(units, slabs);
		double fixedCharge = 50;
		double tax = energyCharge * 0.05;

		Bill bill = new Bill();
		bill.setConsumerId(request.getConsumerId());
		bill.setConnectionId(request.getConnectionId());
		bill.setUtilityType(connection.getUtilityType());
		bill.setTariffPlan(connection.getTariffPlan());

		bill.setBillingMonth(billingMonth);
		bill.setBillingYear(billingYear);

		bill.setConsumptionUnits(units);
		bill.setEnergyCharge(energyCharge);
		bill.setFixedCharge(fixedCharge);
		bill.setTax(tax);
		bill.setPenalty(0);
		bill.setTotalAmount(energyCharge + fixedCharge + tax);

		bill.setStatus(BillStatus.DUE);
		bill.setBillDate(LocalDate.now());
		bill.setDueDate(LocalDate.now().plusDays(15));
		Bill savedBill = billRepository.save(bill);

		try {
			var consumer = consumerClient.getConsumerById(request.getConsumerId());

			BillGeneratedEvent event = new BillGeneratedEvent();
			event.setBillId(savedBill.getId());
			event.setConsumerId(savedBill.getConsumerId());
			event.setEmail(consumer.getEmail());
			event.setUtilityType(savedBill.getUtilityType().name());
			event.setTariffPlan(savedBill.getTariffPlan());
			event.setAmount(savedBill.getTotalAmount());
			event.setDueDate(savedBill.getDueDate().toString());
			billEventPublisher.publish(event);

		} catch (Exception e) {
			log.error("Bill generated but email notification failed", e);
		}

		return map(bill);
	}

	private double calculateFromSlabs(long units, List<TariffSlab> slabs) {

		double amount = 0;
		long remaining = units;

		for (TariffSlab slab : slabs) {
			if (remaining <= 0)
				break;

			long slabUnits = Math.min(remaining, slab.getMaxUnits() - slab.getMinUnits() + 1);

			amount += slabUnits * slab.getRate();
			remaining -= slabUnits;
		}
		return amount;
	}

	public List<BillResponse> getOverdueBills() {
		return billRepository.findByStatus(BillStatus.OVERDUE).stream().map(this::map).toList();
	}

	public void markBillAsPaid(String billId) {

		Bill bill = billRepository.findById(billId)
				.orElseThrow(() -> new ApiException("Bill not found", HttpStatus.NOT_FOUND));

		if (bill.getStatus() == BillStatus.PAID) {
			throw new ApiException("Bill already paid", HttpStatus.BAD_REQUEST);
		}

		bill.setStatus(BillStatus.PAID);
		bill.setTotalAmount(bill.getEnergyCharge() + bill.getFixedCharge() + bill.getTax() + bill.getPenalty());

		billRepository.save(bill);
	}

	public BillResponse getBillById(String billId) {
		return billRepository.findById(billId).map(this::map)
				.orElseThrow(() -> new ApiException("Bill not found", HttpStatus.NOT_FOUND));
	}

	public List<BillResponse> getBillsByConsumer(String consumerId) {

		var bills = billRepository.findByConsumerId(consumerId);
		if (bills.isEmpty()) {
			throw new ApiException("No bills found for consumer", HttpStatus.NOT_FOUND);
		}
		return bills.stream().map(this::map).toList();
	}
	public List<BillResponse> getAllBills(
	        BillStatus status,
	        Integer month,
	        Integer year,
	        String consumerId
	) {

	    List<Bill> bills;

	    if (consumerId != null && status != null) {
	        bills = billRepository.findByConsumerIdAndStatus(consumerId, status);

	    } else if (consumerId != null) {
	        bills = billRepository.findByConsumerId(consumerId);

	    } else if (status != null) {
	        bills = billRepository.findByStatus(status);

	    } else if (month != null && year != null) {
	        bills = billRepository.findByBillingMonthAndBillingYear(month, year);

	    } else {
	        bills = billRepository.findAll();
	    }

	    return bills.stream()
	            .map(this::map)
	            .toList();
	}
	public double getTotalBilledAmount() {
	    return billRepository.findAll()
	            .stream()
	            .mapToDouble(Bill::getTotalAmount)
	            .sum();
	}

	private BillResponse map(Bill bill) {

		BillResponse r = new BillResponse();
		r.setId(bill.getId());
		r.setConsumerId(bill.getConsumerId());
		r.setConnectionId(bill.getConnectionId());

		r.setUtilityType(bill.getUtilityType());
		r.setTariffPlan(bill.getTariffPlan());
		r.setBillingMonth(bill.getBillingMonth());
		r.setBillingYear(bill.getBillingYear());
		r.setEnergyCharge(bill.getEnergyCharge());

		r.setConsumptionUnits(bill.getConsumptionUnits());
		r.setTax(bill.getTax());
		r.setPenalty(bill.getPenalty());

		r.setTotalAmount(bill.getTotalAmount());
		r.setPayableAmount(bill.getTotalAmount() + bill.getPenalty());

		r.setStatus(bill.getStatus());
		r.setDueDate(bill.getDueDate());

		return r;
	}
}