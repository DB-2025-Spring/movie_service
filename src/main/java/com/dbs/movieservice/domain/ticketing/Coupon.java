package com.dbs.movieservice.domain.ticketing;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coupon_seq")
    @SequenceGenerator(name = "coupon_seq", sequenceName = "coupon_seq", allocationSize = 1)
    private Long coupon_id;
    @Column(nullable = false, length = 50)
    private String coupon_name;
    @Column(nullable = false, length = 200)
    private String coupon_description;
    private LocalDate start_date;
    private LocalDate end_date;
}
