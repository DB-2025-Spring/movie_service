package com.dbs.movieservice.service.ticketing;

import com.dbs.movieservice.domain.member.Card;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.domain.ticketing.Ticket;
import com.dbs.movieservice.repository.ticketing.PaymentRepository;
import org.springframework.transaction.annotation.Transactional;
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
    public Payment createPayment(Customer customer, List<Ticket> tickets, Card card, int usePoint, int disCountAmount) {
        //1. ticekts로부터 총 금액 합산.
        int amountValue = tickets.stream().mapToInt( ticket->
                "A".equals(ticket.getAudienceType()) ? 10000:8000).sum();

            int paymentAmount = amountValue - disCountAmount - usePoint;
            if(paymentAmount < 0) {
                throw new RuntimeException("Payment amount is negative");
            }
        //포인트 감소 서비스 호출
        //카드 잔액 감소 서비스 호출

        Payment payment =  savePayment(customer,card,usePoint,disCountAmount,paymentAmount);
        paymentRepository.flush();
        ticketService.confirmPaymentForTicket(tickets, payment);
        //총 결제 금액에 대해 customer의 포인트 증가 서비스 호출
        
        int customerTicketCount = countCustomerTicket(customer);
        //해당값을 등급업 관련 서비스에 전달

        return payment;
    }

    @Transactional
    public Payment cancelPayment(Long paymentId) {
        Payment targetPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        targetPayment.setPaymentStatus("cancelled");
//        int usedPoint = targetPayment.getUsedPoints();
//        int usedMoney = targetPayment.getPaymentAmount();
//        Customer customer = payment.getCustomer();
//        Card card = payment.getCard();
        //usedPoint, usedMoney, customer, card값을 전달하고, 값을 갱신하는 코드 호출
        ticketService.deleteTicket(targetPayment);
        return targetPayment;
    }

    public Payment savePayment(Customer customer, Card card, int usePoint, int disCountAmount, int paymentAmount) {
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setPaymentAmount(paymentAmount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod("카드");
        payment.setDiscountAmount(disCountAmount);
        payment.setUsedPoints(usePoint);
        payment.setApprovalNumber(1);
        payment.setPaymentStatus("Approve");
        return paymentRepository.save(payment);
    }

    public int countCustomerTicket(Customer customer) { //customer의 id로 payment 조회, 그 paymentid를 바탕으로 ticket수 조회
        List<Payment> payments = getApprovedPaymentsByCustomer(customer);
        return payments.stream().mapToInt(ticketService::countCustomerTicket).sum();
    }

    public List<Payment> getApprovedPaymentsByCustomer(Customer customer) {
        return paymentRepository.findByCustomer_CustomerIdAndPaymentStatus(
                customer.getCustomerId(), "Approve"
        );
    }

    /**
     * 고객이 결제한 티켓들에서, 영화를 포함한 정보들을 리턴한다.
     * @param customer
     * @return
     */
    public List<Payment> getAllPaymentByCustomer(Customer customer) {
        return paymentRepository.findPaymentsWithTicketsAndMoviesByCustomerIdAndStatus(customer.getCustomerId(),"Approve");
    }
}
