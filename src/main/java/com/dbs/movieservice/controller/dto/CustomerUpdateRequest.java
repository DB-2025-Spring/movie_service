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
@Schema(description = "고객 프로필 수정 요청 DTO")
public class CustomerUpdateRequest {
    
    @Schema(description = "현재 비밀번호 (비밀번호 변경시 필수)", example = "currentPassword123!")
    private String currentPassword;
    
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()\\-_=+]*$", message = "비밀번호는 영어 소문자, 대문자, 숫자, 특수문자(!@#$%^&*()-_=+)만 가능합니다.")
    @Schema(description = "새 비밀번호 (선택사항)", example = "newPassword123!")
    private String newPassword;
    
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다.")
    @Schema(description = "고객 이름 (선택사항)", example = "홍길동")
    private String customerName;
    
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    @Schema(description = "생년월일 (선택사항)", example = "1990-01-01")
    private LocalDate birthDate;
    
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "전화번호는 000-0000-0000 형식이어야 합니다.")
    @Schema(description = "전화번호 (선택사항)", example = "010-1234-5678")
    private String phone;
} 