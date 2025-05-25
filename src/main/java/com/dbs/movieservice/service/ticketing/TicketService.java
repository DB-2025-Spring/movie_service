package com.dbs.movieservice.service.ticketing;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.ticketing.Ticket;
import com.dbs.movieservice.repository.ticketing.TicketRepository;
import com.dbs.movieservice.service.theater.SeatAvailableService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final SeatAvailableService seatAvailableService;
    public TicketService(TicketRepository ticketRepository, SeatAvailableService seatAvailableService) {
        this.ticketRepository = ticketRepository;
        this.seatAvailableService = seatAvailableService;
    }

    @Transactional
    public boolean createTicketForCustomer(Customer customer, Schedule schedule, List<Seat> seats, int adultNumber) {
        if(!seatAvailableService.isAvailableForTicket(schedule,seats)){
            return false;
        }
        if(!seatAvailableService.updateAvailableSeat(schedule, seats, "T")){
            return false;
        };
        Customer tempCustomer = new Customer();
        tempCustomer.setCustomerId(customer.getCustomerId());
        int adultCount = 0;
        for (Seat seat : seats) {
            Ticket tempTicket = new Ticket();
            tempTicket.setCustomer(tempCustomer);
            tempTicket.setSeat(seat);
            if (adultCount < adultNumber) {
                tempTicket.setAudienceType("A");
                adultCount++;
            } else {
                tempTicket.setAudienceType("Y");
            }
            tempTicket.setIsIssued("T");
            tempTicket.setBookingDatetime(LocalDateTime.now());
            tempTicket.setPayment(null);
            ticketRepository.save(tempTicket);
        };
        return true;
    }

    //todo: 티켓과 결제를 확인하고 삭제/업데이트

}
