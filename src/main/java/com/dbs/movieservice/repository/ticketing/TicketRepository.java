package com.dbs.movieservice.repository.ticketing;

import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.domain.ticketing.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    int countByPayment(Payment payment);
    List<Ticket> findByPayment(Payment payment);
    List<Ticket> findByCustomer_CustomerId(Long customerId);
}
