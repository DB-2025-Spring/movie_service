package com.dbs.movieservice.service.ticketing;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.domain.ticketing.Ticket;
import com.dbs.movieservice.repository.ticketing.TicketRepository;
import com.dbs.movieservice.service.theater.SeatAvailableService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    /**
     * 고객에게 임시 티켓 발급
     * @param customer
     * @param schedule
     * @param seats
     * @param adultNumber
     * @return
     */
    @Transactional
    public List<Ticket> createTicketForCustomer(Customer customer, Schedule schedule, List<Seat> seats, int adultNumber) {
        seatAvailableService.isAvailableForTicket(schedule, seats);
        if (!seatAvailableService.updateAvailableSeat(schedule, seats, "H")) {
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
            tempTicket.setSchedule(schedule);
            if (adultCount < adultNumber) {
                tempTicket.setAudienceType("A");
                adultCount++;
            } else {
                tempTicket.setAudienceType("Y");
            }

            tempTicket.setIsIssued("F");
            tempTicket.setBookingDatetime(LocalDateTime.now());
            tempTicket.setPayment(null);
            Ticket saved = ticketRepository.save(tempTicket); // 저장된 Ticket 받기
            createdTickets.add(saved);
        }
        return createdTickets;
    }

    /**
     * 결제성공 시, ticket에 payment를 부여
     * @param tickets
     * @param payment
     */
    public void confirmPaymentForTicket(List<Ticket> tickets, Payment payment){
        tickets.forEach(ticket->{
            ticket.setPayment(payment);
            ticket.setIsIssued("T");
            seatAvailableService.updateAvailableSeat(ticket.getSchedule(), List.of(ticket.getSeat()),"T");
        });
        ticketRepository.saveAll(tickets);
    }
    public void holdPaymentForTicket(List<Ticket> tickets, Payment payment){
        tickets.forEach(ticket->{
            ticket.setPayment(payment);
            ticket.setIsIssued("H");
        });
        ticketRepository.saveAll(tickets);
    }

    /**
     *
     * @param payment
     * payment에 대해 삭제함. (예매취소)
     */
    public void deleteTicket(Payment payment) {
        List<Ticket> tickets = ticketRepository.findByPayment(payment);
        System.out.println("//////");
        System.out.println(tickets.size());
        List<Seat> seats = tickets.stream().map(Ticket::getSeat).toList();
        seatAvailableService.updateAvailableSeat(tickets.get(0).getSchedule(),seats,"F");
        ticketRepository.deleteAll(tickets);
    }

    /**
     *
     * @param ticketIds
     * 예매 취소가 아닌, 결제 취소 하는경우.
     */
    @Transactional
    public void deleteTicketsBeforePay(List<Long> ticketIds) {

        List<Ticket> tickets = ticketRepository.findAllById(ticketIds);
        if (tickets.size() != ticketIds.size()) {
            throw new IllegalArgumentException("일부 티켓 ID가 존재하지 않습니다.");
        }
        List<Seat> seats = tickets.stream().map(Ticket::getSeat).toList();
        seatAvailableService.updateAvailableSeat(tickets.get(0).getSchedule(),seats,"F");
        ticketRepository.deleteAll(tickets);
    }

    public int countCustomerTicket(Payment payment) {
        return ticketRepository.countByPayment(payment);
    }

    @Transactional(readOnly=true)
    public List<Ticket> getTicketsByIds(List<Long> ticketIds) {
        return ticketRepository.findAllById(ticketIds);
    }


    /**
     * 
     * @param tickets
     * @param customerId
     * 티켓에 대한, customer가 유효한지 검증하는 코드
     */
    public void validateTicketsOwnership(List<Ticket> tickets, Long customerId) {
        boolean hasInvalidOwner = tickets.stream()
                .anyMatch(ticket -> !ticket.getCustomer().getCustomerId().equals(customerId));

        if (hasInvalidOwner) {
            throw new RuntimeException("티켓에 대한 접근 권한이 없습니다.");
        }
    }

    /**
     * 특정 고객이 특정 영화를 관람했는지 확인 (결제완료)
     * @param customerId 고객 ID
     * @param movieId 영화 ID
     * @return 관람 여부
     */
    @Transactional(readOnly = true)
    public boolean hasWatchedMovie(Long customerId, Long movieId) {
        List<Ticket> tickets = ticketRepository.findByCustomer_CustomerId(customerId);
        
        return tickets.stream()
                .filter(ticket -> ticket.getPayment() != null) // 결제가 있는 티켓만
                .filter(ticket -> "Approve".equals(ticket.getPayment().getPaymentStatus())) // 결제 완료
                .anyMatch(ticket -> ticket.getSchedule().getMovie().getMovieId().equals(movieId)); // 해당 영화
    }


}
