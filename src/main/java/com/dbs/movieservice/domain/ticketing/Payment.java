package com.dbs.movieservice.domain.ticketing;

import com.dbs.movieservice.domain.member.Card;
import com.dbs.movieservice.domain.member.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @SequenceGenerator(name = "payment_seq", sequenceName = "payment_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq")
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "payment_amount", nullable = false)
    private Integer paymentAmount;

    @Column(name = "payment_method", length = 20, nullable = false)
    private String paymentMethod;

    @Column(name = "approval_number")
    private Integer approvalNumber;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "discount_amount")
    private Integer discountAmount;

    @Column(name = "used_points")
    private Integer usedPoints;

    @Column(name = "payment_key", length = 255, nullable = true)
    private String paymentKey;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();
}
