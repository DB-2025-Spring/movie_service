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
@Schema(description = "고객 프로필 응답 DTO")
public class CustomerProfileResponse {
    
    @Schema(description = "고객 ID (시퀀스)", example = "1")
    private Long customerId;
    
    @Schema(description = "고객 입력 ID", example = "testuser123")
    private String customerInputId;
    
    @Schema(description = "고객 이름", example = "홍길동")
    private String customerName;
    
    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;
    
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    
    @Schema(description = "가입일", example = "2023-01-15")
    private LocalDate joinDate;
    
    @Schema(description = "보유 포인트", example = "15420")
    private Integer points;
    
    @Schema(description = "고객 등급 ID", example = "3")
    private Integer levelId;
    
    @Schema(description = "고객 등급명", example = "골드")
    private String levelName;
    
    @Schema(description = "리워드율", example = "0.05")
    private Double rewardRate;
} 