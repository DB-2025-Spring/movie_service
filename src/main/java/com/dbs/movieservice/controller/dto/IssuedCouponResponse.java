package com.dbs.movieservice.controller.dto;

import com.dbs.movieservice.domain.member.IssueCoupon;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "발급된 쿠폰 정보 응답 DTO")
public class IssuedCouponResponse {
    
    @Schema(description = "발급 ID", example = "1")
    private Long issueId;
    
    @Schema(description = "쿠폰 ID", example = "1")
    private Long couponId;
    
    @Schema(description = "쿠폰 이름", example = "생일 쿠폰")
    private String couponName;
    
    @Schema(description = "쿠폰 설명", example = "팝콘&콜라 무료 제공")
    private String couponDescription;
    
    @Schema(description = "쿠폰 시작일", example = "2024-01-01")
    private LocalDate startDate;
    
    @Schema(description = "쿠폰 종료일", example = "2024-12-31")
    private LocalDate endDate;
    
    @Schema(description = "사용자 ID", example = "testuser123")
    private String customerInputId;
    
    @Schema(description = "사용자 이름", example = "홍길동")
    private String customerName;
    
    @Schema(description = "쿠폰 발급 시간", example = "2024-01-15T10:30:00")
    private LocalDateTime issuedAt;
    
    @Schema(description = "쿠폰 사용 여부", example = "false")
    private Boolean isUsed;
    
    @Schema(description = "쿠폰 사용 시간", example = "2024-01-20T14:30:00")
    private LocalDateTime usedAt;
    
    @Schema(description = "쿠폰 사용 가능 여부", example = "true")
    private Boolean isUsable;
    
    /**
     * 쿠폰 사용 가능 여부 계산
     */
    public Boolean getIsUsable() {
        if (startDate == null || endDate == null || isUsed == null) {
            return false;
        }
        // 이미 사용된 쿠폰은 사용 불가
        if (isUsed) {
            return false;
        }
        // 날짜 유효성 검사
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }
    
    /**
     * IssueCoupon 엔티티로부터 DTO 생성
     */
    public static IssuedCouponResponse from(IssueCoupon issueCoupon) {
        return IssuedCouponResponse.builder()
                .issueId(issueCoupon.getIssueId())
                .couponId(issueCoupon.getCoupon().getCouponId())
                .couponName(issueCoupon.getCoupon().getCouponName())
                .couponDescription(issueCoupon.getCoupon().getCouponDescription())
                .startDate(issueCoupon.getCoupon().getStartDate())
                .endDate(issueCoupon.getCoupon().getEndDate())
                .customerInputId(issueCoupon.getCustomer().getCustomerInputId())
                .customerName(issueCoupon.getCustomer().getCustomerName())
                .issuedAt(issueCoupon.getIssuedAt())
                .isUsed(issueCoupon.getIsUsed())
                .usedAt(issueCoupon.getUsedAt())
                .build();
    }
} 