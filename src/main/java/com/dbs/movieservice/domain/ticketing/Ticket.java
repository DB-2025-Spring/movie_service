package com.dbs.movieservice.domain.ticketing;


import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.Theater;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @SequenceGenerator(name = "ticket_seq", sequenceName = "ticket_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_seq")
    @Column(name = "ticket_id")
    private Long ticketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "seat_id", referencedColumnName = "seat_id", nullable = false),
            @JoinColumn(name = "theater_id", referencedColumnName = "theater_id", nullable = false)
    })
    private Seat seat;

    @Column(name = "is_issued", length = 1, nullable = false)
    private String isIssued;

    @Column(name = "booking_datetime", nullable = false)
    private LocalDateTime bookingDatetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = true)
    private Payment payment;

    @Column(name = "audience_type", length = 1, nullable = false)
    private String audienceType;
}
