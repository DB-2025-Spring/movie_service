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
@Schema(description = "고객 정보 수정 요청 DTO")
public class CustomerUpdateRequest {
    
    @NotBlank
    @Size(min = 2, max = 50)
    @Schema(description = "고객 이름", example = "홍길동", required = true)
    private String customerName;
    
    @NotBlank
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (010-1234-5678)")
    @Schema(description = "전화번호", example = "010-1234-5678", required = true)
    private String phone;
    
    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;
} 