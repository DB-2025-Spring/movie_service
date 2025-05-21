package com.dbs.movieservice.repository.ticketing;


import com.dbs.movieservice.domain.ticketing.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
