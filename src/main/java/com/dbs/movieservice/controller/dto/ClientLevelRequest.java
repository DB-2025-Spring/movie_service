package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "고객등급 정보 요청 DTO")
public class ClientLevelRequest {
    
    @NotNull
    @Positive
    @Schema(description = "등급 ID", example = "1", required = true)
    private Integer levelId;
    
    @NotBlank
    @Size(min = 1, max = 50)
    @Schema(description = "등급명", example = "VIP", required = true)
    private String levelName;
    
    @NotNull
    @Positive
    @Schema(description = "적립률", example = "0.05", required = true)
    private Double rewardRate;
} 