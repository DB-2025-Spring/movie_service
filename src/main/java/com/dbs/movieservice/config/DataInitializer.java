package com.dbs.movieservice.config;

import com.dbs.movieservice.domain.member.ClientLevel;
import com.dbs.movieservice.domain.ticketing.Coupon;
import com.dbs.movieservice.repository.member.ClientLevelRepository;
import com.dbs.movieservice.repository.ticketing.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 애플리케이션 시작 시 기본 데이터를 초기화하는 컴포넌트
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ClientLevelRepository clientLevelRepository;
    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initClientLevels();
        initCoupons();
    }

    /**
     * 고객 등급 기본 데이터 초기화
     * 1: 브론즈(기본 등급), 2: 실버, 3: 골드, 4: 플래티넘, 5: 다이아몬드
     */
    private void initClientLevels() {
        // 기존 등급 데이터가 있는지 확인
        long count = clientLevelRepository.count();
        
        if (count == 0) {
            log.info("고객 등급 기본 데이터를 초기화합니다.");
            
            List<ClientLevel> defaultLevels = Arrays.asList(
                new ClientLevel(1, "BASIC", 0.00),   // 1: 기본 등급
                new ClientLevel(2, "BRONZE", 0.02),  // 2: 브론즈 등급 (2% 적립)
                new ClientLevel(3, "SILVER", 0.03),  // 3: 실버 등급 (3% 적립)
                new ClientLevel(4, "GOLD", 0.04),    // 4: 골드 등급 (4% 적립)
                new ClientLevel(5, "DIAMOND", 0.05)  // 5: 다이아몬드 등급 (5% 적립)
            );
            
            clientLevelRepository.saveAll(defaultLevels);
            log.info("고객 등급 기본 데이터 초기화 완료: {} 개 등급 생성", defaultLevels.size());
        } else {
            log.info("고객 등급 데이터가 이미 존재합니다. 초기화를 건너뜁니다. 현재 등급 수: {}", count);
        }
    }

    /**
     * 기본 쿠폰 데이터 초기화
     */
    private void initCoupons() {
        // 기본 쿠폰들이 이미 존재하는지 확인
        long couponCount = couponRepository.count();
        
        if (couponCount == 0) {
            log.info("기본 쿠폰 데이터를 초기화합니다.");
            
            LocalDate currentDate = LocalDate.now();

            List<Coupon> defaultCoupons = Arrays.asList(
                createCoupon("생일쿠폰", "생일 축하 특별 할인쿠폰", 
                           LocalDate.of(2024, 1, 1), LocalDate.of(2030, 12, 31)),
                createCoupon("신규가입쿠폰", "신규 회원가입 환영 쿠폰", 
                           currentDate, currentDate.plusMonths(3)),
                createCoupon("등급업쿠폰", "등급 업그레이드 축하 쿠폰", 
                           currentDate, currentDate.plusMonths(3)),
                createCoupon("특별할인쿠폰", "특별 이벤트 할인 쿠폰", 
                           currentDate, currentDate.plusMonths(3))
            );
            
            couponRepository.saveAll(defaultCoupons);
            log.info("기본 쿠폰 데이터 초기화 완료: {} 개 쿠폰 생성", defaultCoupons.size());
        } else {
            log.info("쿠폰 데이터가 이미 존재합니다. 현재 쿠폰 수: {}", couponCount);
        }
    }

    /**
     * 쿠폰 생성 헬퍼 메서드
     */
    private Coupon createCoupon(String name, String description, LocalDate startDate, LocalDate endDate) {
        Coupon coupon = new Coupon();
        coupon.setCouponName(name);
        coupon.setCouponDescription(description);
        coupon.setStartDate(startDate);
        coupon.setEndDate(endDate);
        return coupon;
    }
} 
