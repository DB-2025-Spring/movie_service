package com.dbs.movieservice.service.ticketing;

import com.dbs.movieservice.domain.ticketing.Coupon;
import com.dbs.movieservice.repository.ticketing.CouponRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {
    private final CouponRepository couponRepository;
    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }
    public Coupon getCoupon(Long couponId) {
        return couponRepository.findById(couponId).orElseThrow(()->new EntityNotFoundException("Coupon Not Found"));
    }
    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }
    @Transactional
    public Coupon updateCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Transactional
    public void deleteCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));

        couponRepository.delete(coupon); //
    }

}
