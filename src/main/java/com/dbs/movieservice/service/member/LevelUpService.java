package com.dbs.movieservice.service.member;

import com.dbs.movieservice.domain.member.ClientLevel;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.member.IssueCoupon;
import com.dbs.movieservice.domain.ticketing.Coupon;
import com.dbs.movieservice.repository.member.ClientLevelRepository;
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
public class LevelUpService {

    private final CustomerRepository customerRepository;
    private final ClientLevelRepository clientLevelRepository;
    private final CouponRepository couponRepository;
    private final IssueCouponRepository issueCouponRepository;

    /**
     * 고객의 등급을 업데이트하고, 등급업 시 축하 쿠폰을 발급합니다.
     * 
     * @param customerInputId 고객 ID
     * @param ticketCount 현재까지 구매한 티켓 수
     * @return 등급업 여부
     */
    public boolean processLevelUp(String customerInputId, int ticketCount) {
        try {
            Customer customer = customerRepository.findByCustomerInputId(customerInputId)
                    .orElseThrow(() -> new RuntimeException("Customer not found: " + customerInputId));

            // 현재 등급 저장
            ClientLevel currentLevel = customer.getLevel();
            int currentLevelId = currentLevel.getLevelId();

            log.info("Processing level up for customer: {} (current level: {}, ticket count: {})", 
                    customerInputId, currentLevelId, ticketCount);

            // 새로운 등급 계산
            int newLevelId = calculateNewLevel(ticketCount);

            // 등급업이 필요한지 확인
            if (newLevelId > currentLevelId) {
                // 새로운 등급으로 업데이트
                ClientLevel newLevel = clientLevelRepository.findById(newLevelId)
                        .orElseThrow(() -> new RuntimeException("Level not found: " + newLevelId));

                customer.setLevel(newLevel);
                customerRepository.save(customer);

                log.info("Level up successful! Customer: {} upgraded from level {} to level {}", 
                        customerInputId, currentLevelId, newLevelId);

                // 등급업 축하 쿠폰 발급
                issueLevelUpCoupon(customer);

                return true;
            } else {
                log.debug("No level up needed for customer: {} (current: {}, calculated: {})", 
                        customerInputId, currentLevelId, newLevelId);
                return false;
            }

        } catch (Exception e) {
            log.error("Failed to process level up for customer: {}", customerInputId, e);
            return false;
        }
    }

    /**
     * 티켓 수에 따른 등급 계산
     * 등급 기준:
     * - 1-9장: BASIC (1)
     * - 10-19장: BRONZE (2)  
     * - 20-29장: SILVER (3)
     * - 30-49장: GOLD (4)
     * - 50장 이상: DIAMOND (5)
     */
    private int calculateNewLevel(int ticketCount) {
        if (ticketCount >= 50) {
            return 5; // DIAMOND
        } else if (ticketCount >= 40) {
            return 4; // GOLD
        } else if (ticketCount >= 20) {
            return 3; // SILVER
        } else if (ticketCount >= 10) {
            return 2; // BRONZE
        } else {
            return 1; // BASIC
        }
    }

    /**
     * 등급업 축하 쿠폰 발급
     */
    private void issueLevelUpCoupon(Customer customer) {
        try {
            // 등급업쿠폰 조회 (쿠폰명으로 검색)
            Optional<Coupon> levelUpCouponOpt = couponRepository.findAll().stream()
                    .filter(coupon -> "등급업쿠폰".equals(coupon.getCouponName()))
                    .findFirst();

            if (levelUpCouponOpt.isEmpty()) {
                log.warn("Level up coupon not found, skipping coupon issuance for customer: {}", 
                        customer.getCustomerInputId());
                return;
            }

            Coupon levelUpCoupon = levelUpCouponOpt.get();

            // 이미 등급업쿠폰을 받았는지 확인 (중복 발급 방지)
            boolean alreadyIssued = issueCouponRepository.existsByCustomerIdAndCouponId(
                    customer.getCustomerId(), levelUpCoupon.getCouponId());

            if (alreadyIssued) {
                log.debug("Level up coupon already issued to customer: {}", customer.getCustomerInputId());
                return;
            }

            // 등급업 축하 쿠폰 발급
            IssueCoupon issueCoupon = new IssueCoupon();
            IssueCoupon.IssueCouponId id = new IssueCoupon.IssueCouponId(
                    customer.getCustomerId(), levelUpCoupon.getCouponId());
            issueCoupon.setId(id);
            issueCoupon.setCustomer(customer);
            issueCoupon.setCoupon(levelUpCoupon);

            issueCouponRepository.save(issueCoupon);

            log.info("Level up congratulation coupon issued successfully to customer: {}", 
                    customer.getCustomerInputId());

        } catch (Exception e) {
            log.error("Failed to issue level up coupon to customer: {}", 
                    customer.getCustomerInputId(), e);
        }
    }
} 