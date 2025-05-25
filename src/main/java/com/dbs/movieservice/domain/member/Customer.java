package com.dbs.movieservice.domain.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Customer {
    @Id
    @SequenceGenerator(name="customer_seq",sequenceName = "customer_seq",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @Column(name="customer_id")
    private Long customerId;

    @Column(name = "customer_pw", length = 100, nullable = false)
    private String customerPw;

    @Column(name = "customer_name", length = 100, nullable = false)
    private String customerName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "authority", length = 1)
    private String authority;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "points")
    private Integer points;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private ClientLevel level;
}
