package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "쿠폰 정보 요청 DTO")
public class CouponRequest {
    
    @NotBlank
    @Size(min = 1, max = 50)
    @Schema(description = "쿠폰명", example = "생일 축하 쿠폰", required = true)
    private String couponName;
    
    @NotBlank
    @Size(min = 1, max = 200)
    @Schema(description = "쿠폰 설명", example = "생일을 맞이한 고객을 위한 특별 할인 쿠폰", required = true)
    private String couponDescription;
    
    @NotNull
    @Schema(description = "시작일", example = "2024-04-01", required = true)
    private LocalDate startDate;
    
    @NotNull
    @Schema(description = "종료일", example = "2024-12-31", required = true)
    private LocalDate endDate;
} 