package com.dbs.movieservice.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponRequest {
    
    @NotBlank
    @Size(min = 1, max = 50)
    private String couponName;
    
    @NotBlank
    @Size(min = 1, max = 200)
    private String couponDescription;
    
    @NotNull
    private LocalDate startDate;
    
    @NotNull
    private LocalDate endDate;
} 