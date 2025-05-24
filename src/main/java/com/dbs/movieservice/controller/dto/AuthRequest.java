package com.dbs.movieservice.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    
    @NotBlank
    private String customerInputId;
    
    @NotBlank
    private String customerPw;
} 