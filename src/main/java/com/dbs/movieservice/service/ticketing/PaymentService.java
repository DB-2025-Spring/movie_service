package com.dbs.movieservice.service.ticketing;

import com.dbs.movieservice.config.exception.BusinessException;
import com.dbs.movieservice.domain.member.Card;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.domain.ticketing.Ticket;
import com.dbs.movieservice.dto.ConfirmPaymentDTO;
import com.dbs.movieservice.repository.ticketing.PaymentRepository;
import com.dbs.movieservice.service.member.CardService;
import com.dbs.movieservice.service.member.CustomerService;
import com.dbs.movieservice.service.member.IssueCouponService;
import com.dbs.movieservice.service.member.LevelUpService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final TicketService ticketService;
    private final CardService cardService;
    private final LevelUpService levelUpService;
    private final CustomerService customerService;
    private final CouponService couponService;
    private final IssueCouponService issueCouponService;

    public PaymentService(TicketService ticketService, PaymentRepository paymentRepository, CardService cardService, LevelUpService levelUpService, CustomerService customerService, CouponService couponService, IssueCouponService issueCouponService) {
        this.ticketService = ticketService;
        this.paymentRepository = paymentRepository;
        this.cardService = cardService;
        this.levelUpService = levelUpService;
        this.customerService = customerService;
        this.couponService = couponService;
        this.issueCouponService = issueCouponService;
    }

    @Transactional
    public Payment createPayment(Customer customer, List<Ticket> tickets, int usePoint, int disCountAmount) {
        //1. ticekts로부터 총 금액 합산.
        int amountValue = tickets.stream().mapToInt( ticket->
                "A".equals(ticket.getAudienceType()) ? 10000:8000).sum();

            int paymentAmount = amountValue - disCountAmount - usePoint;
            if(paymentAmount < 0) {
                throw new RuntimeException("Payment amount is negative");
            }
        Payment payment = savePayment(customer, usePoint, disCountAmount, paymentAmount, "Pending");
        paymentRepository.flush();
        ticketService.holdPaymentForTicket(tickets, payment);
        return payment;
    }
    
    //todo 포인트 증감로직 + 등급업(countCustomerTicket(payment.getCustomer())이거 쓰면됨)
    @Transactional
    public ConfirmPaymentDTO confirmPayment(Long paymentId, String paymentKey, int approveNumber, String paymentMethod, Long usedCoupon) {
        // 1. Payment 조회
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역이 존재하지 않습니다."));

        // 2. 상태 확인 및 중복 승인 방지
        if (!"Pending".equals(payment.getPaymentStatus())) {
            throw new IllegalStateException("이미 승인된 결제이거나 상태가 올바르지 않습니다.");
        }

        payment.setPaymentStatus("Approve");
        payment.setPaymentKey(paymentKey);
        payment.setApprovalNumber(approveNumber);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(paymentMethod);
        paymentRepository.save(payment);
        paymentRepository.flush();
        // 4. 연관된 티켓 승인 처리
        List<Ticket> tickets = payment.getTickets(); // @OneToMany(mappedBy = "payment")

        ticketService.confirmPaymentForTicket(tickets, payment);
        System.out.println("1단계 모든 티켓");
        List<Ticket> approveTickets = getAllTicketsByCustomerId(payment.getCustomer());
        System.out.println("///////");
        System.out.println("2단계 아이디");
        System.out.println(payment.getCustomer().getCustomerId());
        System.out.println(approveTickets.size());
        Customer customer = customerService.findByCustomerId(payment.getCustomer().getCustomerId());
        System.out.println(customer.getCustomerInputId());
        System.out.println(customer.getLevel().getRewardRate());
        System.out.println("///////");
        //포인트 사용
        customerService.addCustomerPoint(customer, -1*payment.getUsedPoints());
        customerService.addCustomerPoint(customer, (int) (payment.getPaymentAmount()*customer.getLevel().getRewardRate()));
        levelUpService.processLevelUp(customer.getCustomerInputId(),approveTickets.size());
        // 쿠폰 사용
        issueCouponService.useCouponWhenPayment(customer.getCustomerId(),usedCoupon);
        ConfirmPaymentDTO dto = new ConfirmPaymentDTO();
        dto.setPaymentId(paymentId);
        dto.setCustomerInputId(customer.getCustomerInputId());
        return dto;
    }

    @Transactional
    public Payment cancelPayment(Long paymentId) {
        Payment targetPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        targetPayment.setPaymentStatus("cancelled");
        paymentRepository.save(targetPayment);
        paymentRepository.flush();
        ticketService.deleteTicket(targetPayment);
        return targetPayment;
    }

    public Payment savePayment(Customer customer, int usePoint, int disCountAmount, int paymentAmount,String paymentStatus) {
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setPaymentAmount(paymentAmount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod("카드");
        payment.setDiscountAmount(disCountAmount);
        payment.setUsedPoints(usePoint);
        payment.setApprovalNumber(1);
        payment.setPaymentStatus(paymentStatus);
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

    /**
     * 이 함수를 ticket service에 넣게 되면 순환오류가 발생
     * @param customer
     * @return
     */
    public List<Ticket> getAllTicketsByCustomerId(Customer customer) {
        List<Payment> payments = getAllPaymentByCustomer(customer);
        List<Ticket> tickets = new ArrayList<>();
        for (Payment payment : payments) {
            tickets.addAll(payment.getTickets());
        }
        return tickets;
    }

    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }
}
