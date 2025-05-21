package com.dbs.movieservice.repository.ticketing;

import com.dbs.movieservice.domain.ticketing.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
