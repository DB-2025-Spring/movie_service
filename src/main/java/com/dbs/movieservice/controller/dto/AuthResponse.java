package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class AuthResponse {
    
    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "토큰 타입", example = "Bearer")
    private String type = "Bearer";
    
    @Schema(description = "사용자 ID", example = "testuser")
    private String customerInputId;
    
    @Schema(description = "사용자 권한", example = "MEMBER")
    private String authority;
    
    public AuthResponse(String token, String customerInputId, String authority) {
        this.token = token;
        this.customerInputId = customerInputId;
        this.authority = authority;
    }
} 