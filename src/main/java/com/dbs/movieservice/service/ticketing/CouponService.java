package com.dbs.movieservice.service.ticketing;

import com.dbs.movieservice.domain.ticketing.Coupon;
import com.dbs.movieservice.repository.ticketing.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;

    /**
     * 모든 쿠폰 조회
     */
    @Transactional(readOnly = true)
    public List<Coupon> findAllCoupons() {
        return couponRepository.findAll();
    }

    /**
     * ID로 쿠폰 조회
     */
    @Transactional(readOnly = true)
    public Optional<Coupon> findCouponById(Long couponId) {
        return couponRepository.findById(couponId);
    }

    /**
     * 쿠폰 저장
     */
    @Transactional
    public Coupon saveCoupon(String couponName, String couponDescription, 
                           LocalDate startDate, LocalDate endDate) {
        Coupon coupon = new Coupon();
        coupon.setCouponName(couponName);
        coupon.setCouponDescription(couponDescription);
        coupon.setStartDate(startDate);
        coupon.setEndDate(endDate);
        
        return couponRepository.save(coupon);
    }

    /**
     * 쿠폰 수정
     */
    @Transactional
    public Coupon updateCoupon(Long couponId, String couponName, String couponDescription,
                             LocalDate startDate, LocalDate endDate) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("쿠폰을 찾을 수 없습니다: " + couponId));
        
        coupon.setCouponName(couponName);
        coupon.setCouponDescription(couponDescription);
        coupon.setStartDate(startDate);
        coupon.setEndDate(endDate);
        
        return couponRepository.save(coupon);
    }

    /**
     * 쿠폰 삭제
     */
    @Transactional
    public void deleteCoupon(Long couponId) {
        if (!couponRepository.existsById(couponId)) {
            throw new RuntimeException("쿠폰을 찾을 수 없습니다: " + couponId);
        }
        couponRepository.deleteById(couponId);
    }
} 