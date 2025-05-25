package com.dbs.movieservice.service.ticketing;

import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.repository.ticketing.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final TicketService ticketService;
    public PaymentService(TicketService ticketService, PaymentRepository paymentRepository) {
        this.ticketService = ticketService;
        this.paymentRepository = paymentRepository;
    }

    //need card Transatction
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
