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
@Table(name="coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coupon_seq")
    @SequenceGenerator(name = "coupon_seq", sequenceName = "coupon_seq", allocationSize = 1)
    @Column(name = "coupon_id")
    private Long couponId;
    
    @Column(name = "coupon_name", nullable = false, length = 50)
    private String couponName;
    
    @Column(name = "coupon_description", nullable = false, length = 200)
    private String couponDescription;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "discount_amount", nullable = true, columnDefinition = "INTEGER DEFAULT 2000")
    private Integer discountAmount = 2000;
}
