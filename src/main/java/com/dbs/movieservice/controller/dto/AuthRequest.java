package com.dbs.movieservice.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    
    @NotBlank(message = "아이디를 입력해주세요.")
    private String customerInputId;
    
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String customerPw;
} 