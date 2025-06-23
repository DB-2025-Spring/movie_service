package com.dbs.movieservice.repository.member;

import com.dbs.movieservice.domain.member.IssueCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IssueCouponRepository extends JpaRepository<IssueCoupon, Long> {
    
    /**
     * 특정 고객의 발급된 쿠폰 목록 조회 (customerId 기준) - 최신순 정렬
     */
    @Query("SELECT ic FROM IssueCoupon ic " +
           "JOIN FETCH ic.coupon c " +
           "JOIN FETCH ic.customer cu " +
           "WHERE cu.customerId = :customerId " +
           "ORDER BY ic.issuedAt DESC")
    List<IssueCoupon> findIssuedCouponsByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * 특정 고객의 발급된 쿠폰 목록 조회 (customerInputId 기준) - 최신순 정렬
     */
    @Query("SELECT ic FROM IssueCoupon ic " +
           "JOIN FETCH ic.coupon c " +
           "JOIN FETCH ic.customer cu " +
           "WHERE cu.customerInputId = :customerInputId " +
           "ORDER BY ic.issuedAt DESC")
    List<IssueCoupon> findIssuedCouponsByCustomerInputId(@Param("customerInputId") String customerInputId);
    
    /**
     * 특정 쿠폰이 특정 고객에게 발급되었는지 확인
     */
    @Query("SELECT COUNT(ic) > 0 FROM IssueCoupon ic " +
           "WHERE ic.customer.customerId = :customerId AND ic.coupon.couponId = :couponId")
    boolean existsByCustomerIdAndCouponId(@Param("customerId") Long customerId, @Param("couponId") Long couponId);
    
    /**
     * 특정 고객의 특정 쿠폰 발급 내역 조회
     */
    @Query("SELECT ic FROM IssueCoupon ic " +
           "WHERE ic.customer.customerId = :customerId AND ic.coupon.couponId = :couponId")
    List<IssueCoupon> findByCustomerIdAndCouponId(@Param("customerId") Long customerId, @Param("couponId") Long couponId);
    
    /**
     * 특정 고객의 사용 가능한 쿠폰 목록 조회 (미사용 + 유효한 쿠폰)
     */
    @Query("SELECT ic FROM IssueCoupon ic " +
           "JOIN FETCH ic.coupon c " +
           "JOIN FETCH ic.customer cu " +
           "WHERE cu.customerId = :customerId " +
           "AND ic.isUsed = false " +
           "AND c.endDate >= CURRENT_DATE " +
           "ORDER BY ic.issuedAt DESC")
    List<IssueCoupon> findAvailableCouponsByCustomerId(@Param("customerId") Long customerId);

    Optional<IssueCoupon> findByCustomer_CustomerIdAndCoupon_CouponIdAndIsUsedFalse(Long customerId, Long couponId);
}
