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
     * 현재 로그인한 사용자의 발급된 쿠폰 목록 조회 (최신순)
     */
    @Transactional(readOnly = true)
    public List<IssuedCouponResponse> getIssuedCouponsByCustomerInputId(String customerInputId) {
        log.info("Retrieving issued coupons for customer: {}", customerInputId);
        
        List<IssueCoupon> issueCoupons = issueCouponRepository.findIssuedCouponsByCustomerInputId(customerInputId);
        
        return issueCoupons.stream()
                .map(IssuedCouponResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 사용 가능한 쿠폰만 조회 (미사용 + 유효한 쿠폰)
     */
    @Transactional(readOnly = true)
    public List<IssuedCouponResponse> getUsableCouponsByCustomerInputId(String customerInputId) {
        log.info("Retrieving usable coupons for customer: {}", customerInputId);
        
        Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));
        
        List<IssueCoupon> issueCoupons = issueCouponRepository.findAvailableCouponsByCustomerId(customer.getCustomerId());
        
        return issueCoupons.stream()
                .map(IssuedCouponResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 고객에게 쿠폰 발급 (중복 발급 허용)
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
        
        // IssueCoupon 생성 및 저장 (새로운 구조에서는 중복 발급 허용)
        IssueCoupon issueCoupon = new IssueCoupon();
        issueCoupon.setCustomer(customer);
        issueCoupon.setCoupon(coupon);
        
        IssueCoupon savedIssueCoupon = issueCouponRepository.save(issueCoupon);
        
        log.info("Successfully issued coupon {} to customer {} with issue ID {}", 
                couponId, customerInputId, savedIssueCoupon.getIssueId());
        return IssuedCouponResponse.from(savedIssueCoupon);
    }
    
    /**
     * 쿠폰 발급 취소 (관리자용) - 가장 최근 발급된 쿠폰 취소
     */
    @Transactional
    public void revokeCouponFromCustomer(String customerInputId, Long couponId) {
        log.info("Revoking coupon {} from customer {}", couponId, customerInputId);
        
        Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));
        
        // 해당 고객의 특정 쿠폰 발급 내역 조회 (최신순)
        List<IssueCoupon> issueCoupons = issueCouponRepository.findByCustomerIdAndCouponId(
                customer.getCustomerId(), couponId);
        
        if (issueCoupons.isEmpty()) {
            throw new RuntimeException("Coupon not issued to this customer");
        }
        
        // 가장 최근 발급된 쿠폰 삭제 (리스트의 첫 번째 항목이 최신)
        IssueCoupon latestIssue = issueCoupons.get(0);
        issueCouponRepository.deleteById(latestIssue.getIssueId());
        
        log.info("Successfully revoked coupon {} (issue ID: {}) from customer {}", 
                couponId, latestIssue.getIssueId(), customerInputId);
    }
    
    /**
     * 쿠폰 사용 처리
     */
    @Transactional
    public void useCoupon(Long issueId) {
        log.info("Using coupon with issue ID: {}", issueId);
        
        IssueCoupon issueCoupon = issueCouponRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issued coupon not found: " + issueId));
        
        if (issueCoupon.getIsUsed()) {
            throw new RuntimeException("Coupon already used");
        }
        
        issueCoupon.useCoupon();
        issueCouponRepository.save(issueCoupon);
        
        log.info("Successfully used coupon with issue ID: {}", issueId);
    }

    public void useCouponWhenPayment(Long customerId, Long couponId) {
        IssueCoupon issueCoupon = issueCouponRepository
                .findByCustomer_CustomerIdAndCoupon_CouponIdAndIsUsedFalse(customerId, couponId)
                .orElseThrow(() -> new IllegalArgumentException("사용 가능한 쿠폰이 존재하지 않습니다."));

        issueCoupon.useCoupon();
    }

    /**
     * 쿠폰 사용 취소
     */
    @Transactional
    public void cancelCouponUsage(Long issueId) {
        log.info("Canceling coupon usage for issue ID: {}", issueId);
        
        IssueCoupon issueCoupon = issueCouponRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issued coupon not found: " + issueId));
        
        if (!issueCoupon.getIsUsed()) {
            throw new RuntimeException("Coupon is not used");
        }
        
        issueCoupon.cancelUsage();
        issueCouponRepository.save(issueCoupon);
        
        log.info("Successfully canceled coupon usage for issue ID: {}", issueId);
    }
    
    /**
     * 특정 고객이 특정 쿠폰을 보유하고 있는지 확인 (사용 가능한 쿠폰만)
     */
    @Transactional(readOnly = true)
    public boolean hasUsableCoupon(String customerInputId, Long couponId) {
        Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));
        
        List<IssueCoupon> availableCoupons = issueCouponRepository.findAvailableCouponsByCustomerId(customer.getCustomerId());
        return availableCoupons.stream()
                .anyMatch(ic -> ic.getCoupon().getCouponId().equals(couponId));
    }
    
    /**
     * 특정 고객이 특정 쿠폰을 발급받은 적이 있는지 확인 (모든 발급 내역)
     */
    @Transactional(readOnly = true)
    public boolean hasCoupon(String customerInputId, Long couponId) {
        Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));
        
        return issueCouponRepository.existsByCustomerIdAndCouponId(customer.getCustomerId(), couponId);
    }
} 