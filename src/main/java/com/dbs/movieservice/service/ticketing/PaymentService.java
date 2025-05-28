package com.dbs.movieservice.service.ticketing;

import com.dbs.movieservice.domain.member.Card;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.domain.ticketing.Ticket;
import com.dbs.movieservice.repository.ticketing.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final TicketService ticketService;
    public PaymentService(TicketService ticketService, PaymentRepository paymentRepository) {
        this.ticketService = ticketService;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void createPayment(Customer customer, List<Ticket> tickets, Card card, int usePoint, int disCountAmount) {
        //1. ticekts로부터 총 금액 합산.
        int amountValue = tickets.stream().mapToInt( ticket->
                "A".equals(ticket.getAudienceType()) ? 10000:8000).sum();
        int paymentAmount = amountValue - disCountAmount-usePoint;

        //포인트 감소 서비스 호출
        //카드 잔액 감소 서비스 호출
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setPaymentAmount(paymentAmount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(card.getCardCompany());
        payment.setDiscountAmount(disCountAmount);
        payment.setUsedPoints(usePoint);
        paymentRepository.save(payment);
        ticketService.confirmPaymentForTicket(tickets, payment);
        //총 결제 금액에 대해 customer의 포인트 증가 서비스 호출
        
        //userId로 이떄까지 결제한 ticket수 계산
    }

    public int countCustomerTicket(Customer customer) { //customer의 id로 payment 조회, 그 paymentid를 바탕으로 ticket수 조회
        return 0;
    }
}
