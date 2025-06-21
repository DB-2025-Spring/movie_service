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
@Schema(description = "비회원 가입 응답 DTO")
public class GuestSignupResponse {
    
    @Schema(description = "시스템 생성 아이디", example = "GUEST20241215001")
    private String customerInputId;
    
    @Schema(description = "사용자 이름", example = "홍길동")
    private String customerName;
    
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    
    @Schema(description = "가입일", example = "2024-12-15")
    private LocalDate joinDate;
    
    @Schema(description = "메시지", example = "비회원 등록이 완료되었습니다.")
    private String message;
    
    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "토큰 타입", example = "Bearer")
    private String type = "Bearer";
    
    @Schema(description = "사용자 권한", example = "GUEST")
    private String authority;
} 