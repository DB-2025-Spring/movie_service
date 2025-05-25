package com.dbs.movieservice.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatRequest {
    
    @NotNull
    @Positive
    private Long theaterId;
    
    @NotNull
    @Positive
    private Integer rowNumber;
    
    @NotNull
    @Positive
    private Integer columnNumber;
} 