package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "비회원 가입 요청 DTO")
public class GuestSignupRequest {
    
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다.")
    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    private String customerName;
    
    @NotNull(message = "법정생년월일은 필수입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    @Schema(description = "법정생년월일", example = "1990-01-01", required = true)
    private LocalDate birthDate;
    
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "전화번호는 000-0000-0000 형식이어야 합니다.")
    @Schema(description = "전화번호", example = "010-1234-5678", required = true)
    private String phone;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, max = 6, message = "비밀번호는 4자 이상 6자 이하여야 합니다.")
    @Pattern(regexp = "^[0-9]*$", message = "비밀번호는 숫자만 가능합니다.")
    @Schema(description = "비밀번호 (숫자 4-6자리)", example = "1234", required = true, format = "password")
    private String customerPw;
} 