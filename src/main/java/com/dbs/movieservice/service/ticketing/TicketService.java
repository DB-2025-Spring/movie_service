package com.dbs.movieservice.service.ticketing;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.ticketing.Ticket;
import com.dbs.movieservice.repository.ticketing.TicketRepository;
import com.dbs.movieservice.service.theater.SeatAvailableService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final SeatAvailableService seatAvailableService;
    public TicketService(TicketRepository ticketRepository, SeatAvailableService seatAvailableService) {
        this.ticketRepository = ticketRepository;
        this.seatAvailableService = seatAvailableService;
    }

    @Transactional
    public void createTicketForCustomer(Customer customer, Schedule schedule, List<Seat> seats) {

    }

}
