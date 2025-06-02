package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class AuthRequest {
    
    @NotBlank(message = "아이디를 입력해주세요.")
    @Schema(description = "사용자 ID", example = "testuser", required = true)
    private String customerInputId;
    
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "비밀번호", example = "password123!", required = true, format = "password")
    private String customerPw;
} 