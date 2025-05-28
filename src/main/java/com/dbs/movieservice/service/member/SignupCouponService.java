package com.dbs.movieservice.service.member;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.member.IssueCoupon;
import com.dbs.movieservice.domain.ticketing.Coupon;
import com.dbs.movieservice.repository.member.CustomerRepository;
import com.dbs.movieservice.repository.member.IssueCouponRepository;
import com.dbs.movieservice.repository.ticketing.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SignupCouponService {

    private final CustomerRepository customerRepository;
    private final CouponRepository couponRepository;
    private final IssueCouponRepository issueCouponRepository;

    /**
     * 신규 회원가입한 사용자에게 신규가입쿠폰 발급
     * 
     * @param customerInputId 사용자 ID
     * @return 신규가입쿠폰 발급 성공 여부
     */
    public boolean issueSignupCoupon(String customerInputId) {
        try {
            Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                    .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));

            log.info("Processing signup coupon for new user: {}", customerInputId);

            // 신규가입쿠폰 조회
            Optional<Coupon> signupCouponOpt = couponRepository.findAll().stream()
                    .filter(coupon -> "신규가입쿠폰".equals(coupon.getCouponName()))
                    .findFirst();

            if (signupCouponOpt.isEmpty()) {
                log.warn("Signup coupon not found, skipping coupon issuance for user: {}", customerInputId);
                return false;
            }

            Coupon signupCoupon = signupCouponOpt.get();

            // 이미 신규가입쿠폰을 받았는지 확인 (중복 발급 방지)
            boolean alreadyIssued = issueCouponRepository.existsByCustomerIdAndCouponId(
                    customer.getCustomerId(), signupCoupon.getCouponId());

            if (alreadyIssued) {
                log.debug("Signup coupon already issued to customer: {}", customerInputId);
                return false;
            }

            // 신규가입쿠폰 발급
            IssueCoupon issueCoupon = new IssueCoupon();
            IssueCoupon.IssueCouponId id = new IssueCoupon.IssueCouponId(
                    customer.getCustomerId(), signupCoupon.getCouponId());
            issueCoupon.setId(id);
            issueCoupon.setCustomer(customer);
            issueCoupon.setCoupon(signupCoupon);

            issueCouponRepository.save(issueCoupon);

            log.info("Signup coupon issued successfully to user: {}", customerInputId);
            return true;

        } catch (Exception e) {
            log.error("Failed to issue signup coupon to user: {}", customerInputId, e);
            return false;
        }
    }
} 