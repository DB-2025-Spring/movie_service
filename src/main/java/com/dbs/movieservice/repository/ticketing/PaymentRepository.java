package com.dbs.movieservice.repository.ticketing;

import com.dbs.movieservice.domain.ticketing.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
