package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "비회원 로그인(예매 조회) 요청 DTO")
public class GuestLoginRequest {
    
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "전화번호는 000-0000-0000 형식이어야 합니다.")
    @Schema(description = "전화번호", example = "010-1234-5678", required = true)
    private String phone;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(description = "비밀번호", example = "1234", required = true, format = "password")
    private String password;
} 