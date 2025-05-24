package com.dbs.movieservice.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    
    @NotBlank
    @Size(min = 3, max = 20)
    private String customerInputId;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String customerPw;
    
    @NotBlank
    @Size(min = 2, max = 50)
    private String customerName;
    
    private LocalDate birthDate;
    
    private String phone;
} 