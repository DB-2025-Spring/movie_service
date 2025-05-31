package com.dbs.movieservice.service.ticketing;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.domain.ticketing.Ticket;
import com.dbs.movieservice.repository.ticketing.TicketRepository;
import com.dbs.movieservice.service.theater.SeatAvailableService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final SeatAvailableService seatAvailableService;
    public TicketService(TicketRepository ticketRepository, SeatAvailableService seatAvailableService) {
        this.ticketRepository = ticketRepository;
        this.seatAvailableService = seatAvailableService;
    }

//    @Transactional
//    public boolean createTicketForCustomer(Customer customer, Schedule schedule, List<Seat> seats, int adultNumber) {
//        if(!seatAvailableService.isAvailableForTicket(schedule,seats)){
//            return false;
//        }
//        if(!seatAvailableService.updateAvailableSeat(schedule, seats, "T")){
//            return false;
//        };
//        Customer tempCustomer = new Customer();
//        tempCustomer.setCustomerId(customer.getCustomerId());
//        int adultCount = 0;
//        for (Seat seat : seats) {
//            Ticket tempTicket = new Ticket();
//            tempTicket.setCustomer(tempCustomer);
//            tempTicket.setSeat(seat);
//            if (adultCount < adultNumber) {
//                tempTicket.setAudienceType("A");
//                adultCount++;
//            } else {
//                tempTicket.setAudienceType("Y");
//            }
//            tempTicket.setIsIssued("T");
//            tempTicket.setBookingDatetime(LocalDateTime.now());
//            tempTicket.setPayment(null);
//            ticketRepository.save(tempTicket);
//        };
//        return true;
//    }
    @Transactional
    public List<Ticket> createTicketForCustomer(Customer customer, Schedule schedule, List<Seat> seats, int adultNumber) {
        if (!seatAvailableService.isAvailableForTicket(schedule, seats)) {
            return List.of(); // 실패 시 빈 리스트 반환
        }

        if (!seatAvailableService.updateAvailableSeat(schedule, seats, "T")) {
            return List.of(); // 실패 시 빈 리스트 반환
        }

        Customer tempCustomer = new Customer();
        tempCustomer.setCustomerId(customer.getCustomerId());

        List<Ticket> createdTickets = new ArrayList<>();
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
            Ticket saved = ticketRepository.save(tempTicket); // 저장된 Ticket 받기
            createdTickets.add(saved);
        }
        return createdTickets;
    }

    public void confirmPaymentForTicket(List<Ticket> tickets, Payment payment){
        tickets.forEach(ticket->{
            ticket.setPayment(payment);
            seatAvailableService.updateAvailableSeat(ticket.getSchedule(), List.of(ticket.getSeat()),"F");
        });
        ticketRepository.saveAll(tickets);
    }

    //todo: 티켓과 결제를 확인하고 삭제/업데이트
    //이 함수는 payment에서 호출 될 함수이기에 transaction을 선언안함.
    public void deleteTicket(Payment payment) {
        List<Ticket> tickets = ticketRepository.findByPayment(payment);
        List<Seat> seats = tickets.stream().map(Ticket::getSeat).toList();
        seatAvailableService.updateAvailableSeat(tickets.get(0).getSchedule(),seats,"F");
        ticketRepository.deleteAll(tickets);
    }
    public int countCustomerTicket(Payment payment) {
        return ticketRepository.countByPayment(payment);
    }

    @Transactional(readOnly=true)
    public List<Ticket> getTicketsByIds(List<Long> ticketIds) {
        return ticketIds.stream()
                .map(ticketRepository::getReferenceById) // 또는 getById
                .toList();
    }
}
