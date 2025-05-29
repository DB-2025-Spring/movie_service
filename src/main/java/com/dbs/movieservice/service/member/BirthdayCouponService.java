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

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BirthdayCouponService {

    private final CustomerRepository customerRepository;
    private final CouponRepository couponRepository;
    private final IssueCouponRepository issueCouponRepository;

    /**
     * 생일인 사용자에게 생일쿠폰 발급
     * 
     * @param customerInputId 사용자 ID
     * @return 생일쿠폰 발급 성공 여부
     */
    public boolean issueBirthdayCouponIfEligible(String customerInputId) {
        try {
            Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                    .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));

            // 생일 정보가 없으면 패스
            if (customer.getBirthDate() == null) {
                log.debug("No birthday information for user: {}", customerInputId);
                return false;
            }

            // 오늘이 생일인지 확인
            LocalDate today = LocalDate.now();
            LocalDate birthDate = customer.getBirthDate();

            boolean isBirthday = today.getMonth() == birthDate.getMonth() && 
                               today.getDayOfMonth() == birthDate.getDayOfMonth();

            if (!isBirthday) {
                log.debug("Today is not birthday for user: {}", customerInputId);
                return false;
            }

            log.info("Birthday detected for user: {} (birthday: {})", customerInputId, birthDate);

            // 생일쿠폰 조회
            Optional<Coupon> birthdayCouponOpt = couponRepository.findAll().stream()
                    .filter(coupon -> "생일쿠폰".equals(coupon.getCouponName()))
                    .findFirst();

            if (birthdayCouponOpt.isEmpty()) {
                log.warn("Birthday coupon not found, skipping coupon issuance for user: {}", customerInputId);
                return false;
            }

            Coupon birthdayCoupon = birthdayCouponOpt.get();

            // 올해 이미 생일쿠폰을 받았는지 확인
            boolean alreadyIssued = hasReceivedBirthdayCouponThisYear(customer.getCustomerId(), birthdayCoupon.getCouponId());

            if (alreadyIssued) {
                log.debug("Birthday coupon already issued this year for user: {}", customerInputId);
                return false;
            }

            // 생일쿠폰 발급
            IssueCoupon issueCoupon = new IssueCoupon();
            IssueCoupon.IssueCouponId id = new IssueCoupon.IssueCouponId(
                    customer.getCustomerId(), birthdayCoupon.getCouponId());
            issueCoupon.setId(id);
            issueCoupon.setCustomer(customer);
            issueCoupon.setCoupon(birthdayCoupon);

            issueCouponRepository.save(issueCoupon);

            log.info("Birthday coupon issued successfully to user: {}", customerInputId);
            return true;

        } catch (Exception e) {
            log.error("Failed to issue birthday coupon to user: {}", customerInputId, e);
            return false;
        }
    }

    /**
     * 올해 생일쿠폰을 이미 받았는지 확인
     * IssueCoupon에 issuedAt 필드가 없으므로, 단순히 발급 여부만 확인
     * 실제 구현에서는 년도별 체크 로직이 필요할 수 있습니다.
     */
    private boolean hasReceivedBirthdayCouponThisYear(Long customerId, Long couponId) {
        // 현재는 단순히 발급 여부만 확인
        // 추후 issuedAt 필드가 추가되면 년도별 체크 로직으로 변경 가능
        return issueCouponRepository.existsByCustomerIdAndCouponId(customerId, couponId);
    }
} 