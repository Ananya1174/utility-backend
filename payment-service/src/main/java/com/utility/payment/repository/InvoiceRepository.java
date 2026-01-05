package com.utility.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.utility.payment.model.Invoice;

public interface InvoiceRepository
extends MongoRepository<Invoice, String> {

List<Invoice> findByConsumerId(String consumerId);

Optional<Invoice> findByPaymentId(String paymentId);
Optional<Invoice> findByBillId(String billId);
}