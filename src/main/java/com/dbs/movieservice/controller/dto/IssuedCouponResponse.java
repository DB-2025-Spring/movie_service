package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "발급된 쿠폰 정보 응답 DTO")
public class IssuedCouponResponse {
    
    @Schema(description = "쿠폰 ID", example = "1")
    private Long couponId;
    
    @Schema(description = "쿠폰 이름", example = "생일 쿠폰")
    private String couponName;
    
    @Schema(description = "쿠폰 설명", example = "팝콘 무료 제공")
    private String couponDescription;
    
    @Schema(description = "쿠폰 시작일", example = "2024-01-01")
    private LocalDate startDate;
    
    @Schema(description = "쿠폰 종료일", example = "2024-12-31")
    private LocalDate endDate;
    
    @Schema(description = "쿠폰 사용 가능 여부", example = "true")
    private Boolean isUsable;
    
    @Schema(description = "사용자 ID", example = "testuser123")
    private String customerInputId;
    
    @Schema(description = "사용자 이름", example = "홍길동")
    private String customerName;
    
    /**
     * 쿠폰 사용 가능 여부 계산
     */
    public Boolean getIsUsable() {
        if (startDate == null || endDate == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }
} 