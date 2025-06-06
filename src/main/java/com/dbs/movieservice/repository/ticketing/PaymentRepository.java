package com.dbs.movieservice.repository.ticketing;

import com.dbs.movieservice.domain.ticketing.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCustomer_CustomerIdAndPaymentStatus(Long customerId, String paymentStatus);

    @Query("SELECT DISTINCT p from Payment p LEFT JOIN FETCH p.tickets WHERE p.customer.customerId = :customerId and p.paymentStatus= :status")
    List<Payment> findByCustomerIdWithStatus(@Param("customerId") Long customerId, @Param("status") String status);

    @Query("""
    SELECT DISTINCT p 
    FROM Payment p
    LEFT JOIN FETCH p.tickets t
    LEFT JOIN FETCH t.schedule s
    LEFT JOIN FETCH s.movie m
    WHERE p.customer.customerId = :customerId 
    AND p.paymentStatus = :status
""")
    List<Payment> findPaymentsWithTicketsAndMoviesByCustomerIdAndStatus(
            @Param("customerId") Long customerId,
            @Param("status") String status
    );
}

