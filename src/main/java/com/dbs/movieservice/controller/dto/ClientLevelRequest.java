package com.dbs.movieservice.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientLevelRequest {
    
    @NotNull
    @Positive
    private Integer levelId;
    
    @NotBlank
    @Size(min = 1, max = 50)
    private String levelName;
    
    @NotNull
    @Positive
    private Double rewardRate;
} 