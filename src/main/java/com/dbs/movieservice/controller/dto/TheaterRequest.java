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
@Schema(description = "상영관 정보 요청 DTO")
public class TheaterRequest {
    
    @NotBlank
    @Size(min = 1, max = 100)
    @Schema(description = "상영관명", example = "1관", required = true)
    private String theaterName;
    
    @NotNull
    @Positive
    @Schema(description = "총 좌석수", example = "100", required = true)
    private Integer totalSeats;
} 