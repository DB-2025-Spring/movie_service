package com.dbs.movieservice.service.member;

import com.dbs.movieservice.controller.dto.IssuedCouponResponse;
import com.dbs.movieservice.domain.member.IssueCoupon;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.ticketing.Coupon;
import com.dbs.movieservice.repository.member.IssueCouponRepository;
import com.dbs.movieservice.repository.member.CustomerRepository;
import com.dbs.movieservice.repository.ticketing.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IssueCouponService {
    
    private final IssueCouponRepository issueCouponRepository;
    private final CustomerRepository customerRepository;
    private final CouponRepository couponRepository;
    
    /**
     * 현재 로그인한 사용자의 발급된 쿠폰 목록 조회
     */
    @Transactional(readOnly = true)
    public List<IssuedCouponResponse> getIssuedCouponsByCustomerInputId(String customerInputId) {
        log.info("Retrieving issued coupons for customer: {}", customerInputId);
        
        List<IssueCoupon> issueCoupons = issueCouponRepository.findIssuedCouponsByCustomerInputId(customerInputId);
        
        return issueCoupons.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 사용 가능한 쿠폰만 조회
     */
    @Transactional(readOnly = true)
    public List<IssuedCouponResponse> getUsableCouponsByCustomerInputId(String customerInputId) {
        log.info("Retrieving usable coupons for customer: {}", customerInputId);
        
        List<IssueCoupon> issueCoupons = issueCouponRepository.findIssuedCouponsByCustomerInputId(customerInputId);
        LocalDate now = LocalDate.now();
        
        return issueCoupons.stream()
                .filter(issueCoupon -> {
                    Coupon coupon = issueCoupon.getCoupon();
                    return coupon.getStartDate() != null && 
                           coupon.getEndDate() != null &&
                           !now.isBefore(coupon.getStartDate()) && 
                           !now.isAfter(coupon.getEndDate());
                })
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 고객에게 쿠폰 발급
     */
    @Transactional
    public IssuedCouponResponse issueCouponToCustomer(String customerInputId, Long couponId) {
        log.info("Issuing coupon {} to customer {}", couponId, customerInputId);
        
        // 고객 조회
        Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));
        
        // 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found: " + couponId));
        
        // 중복 발급 체크
        if (issueCouponRepository.existsByCustomerIdAndCouponId(customer.getCustomerId(), couponId)) {
            throw new RuntimeException("Coupon already issued to this customer");
        }
        
        // IssueCoupon 생성 및 저장
        IssueCoupon issueCoupon = new IssueCoupon();
        issueCoupon.setId(new IssueCoupon.IssueCouponId(customer.getCustomerId(), couponId));
        issueCoupon.setCustomer(customer);
        issueCoupon.setCoupon(coupon);
        
        IssueCoupon savedIssueCoupon = issueCouponRepository.save(issueCoupon);
        
        log.info("Successfully issued coupon {} to customer {}", couponId, customerInputId);
        return convertToResponse(savedIssueCoupon);
    }
    
    /**
     * 쿠폰 발급 취소 (관리자용)
     */
    @Transactional
    public void revokeCouponFromCustomer(String customerInputId, Long couponId) {
        log.info("Revoking coupon {} from customer {}", couponId, customerInputId);
        
        Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));
        
        IssueCoupon.IssueCouponId id = new IssueCoupon.IssueCouponId(customer.getCustomerId(), couponId);
        
        if (!issueCouponRepository.existsById(id)) {
            throw new RuntimeException("Coupon not issued to this customer");
        }
        
        issueCouponRepository.deleteById(id);
        log.info("Successfully revoked coupon {} from customer {}", couponId, customerInputId);
    }
    
    /**
     * IssueCoupon을 IssuedCouponResponse로 변환
     */
    private IssuedCouponResponse convertToResponse(IssueCoupon issueCoupon) {
        Coupon coupon = issueCoupon.getCoupon();
        Customer customer = issueCoupon.getCustomer();
        
        return IssuedCouponResponse.builder()
                .couponId(coupon.getCouponId())
                .couponName(coupon.getCouponName())
                .couponDescription(coupon.getCouponDescription())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .customerInputId(customer.getCustomerInputId())
                .customerName(customer.getCustomerName())
                .build();
    }
    
    /**
     * 특정 고객이 특정 쿠폰을 보유하고 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean hasCoupon(String customerInputId, Long couponId) {
        Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));
        
        return issueCouponRepository.existsByCustomerIdAndCouponId(customer.getCustomerId(), couponId);
    }
} 