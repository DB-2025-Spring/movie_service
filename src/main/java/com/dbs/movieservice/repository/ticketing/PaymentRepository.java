package com.dbs.movieservice.repository.ticketing;

import com.dbs.movieservice.domain.ticketing.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCustomer_CustomerIdAndPaymentStatus(Long customerId, String paymentStatus);
}
