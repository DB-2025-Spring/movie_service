package com.dbs.movieservice.controller.dto;

import com.dbs.movieservice.domain.member.Customer;
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
@Schema(description = "고객 정보 응답 DTO")
public class CustomerResponse {
    
    @Schema(description = "고객 ID", example = "1")
    private Long customerId;
    
    @Schema(description = "고객 입력 ID", example = "testuser123")
    private String customerInputId;
    
    @Schema(description = "고객 이름", example = "홍길동")
    private String customerName;
    
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    
    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;
    
    @Schema(description = "가입일", example = "2024-01-01")
    private LocalDate joinDate;
    
    @Schema(description = "권한", example = "M")
    private String authority;
    
    @Schema(description = "포인트", example = "1000")
    private Integer points;
    
    @Schema(description = "등급 정보")
    private LevelInfo level;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "등급 정보")
    public static class LevelInfo {
        @Schema(description = "등급 ID", example = "1")
        private Integer levelId;
        
        @Schema(description = "등급명", example = "브론즈")
        private String levelName;
    }
    
    /**
     * Customer 엔티티를 CustomerResponse로 변환
     */
    public static CustomerResponse from(Customer customer) {
        LevelInfo levelInfo = null;
        if (customer.getLevel() != null) {
            levelInfo = LevelInfo.builder()
                    .levelId(customer.getLevel().getLevelId())
                    .levelName(customer.getLevel().getLevelName())
                    .build();
        }
        
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .customerInputId(customer.getCustomerInputId())
                .customerName(customer.getCustomerName())
                .phone(customer.getPhone())
                .birthDate(customer.getBirthDate())
                .joinDate(customer.getJoinDate())
                .authority(customer.getAuthority())
                .points(customer.getPoints())
                .level(levelInfo)
                .build();
    }
} 