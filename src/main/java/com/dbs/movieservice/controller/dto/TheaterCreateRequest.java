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
@Schema(description = "상영관 생성 요청 DTO - 좌석 배치 포함")
public class TheaterCreateRequest {
    
    @NotBlank
    @Size(min = 1, max = 100)
    @Schema(description = "상영관명", example = "1관", required = true)
    private String theaterName;
    
    @NotNull
    @Positive
    @Schema(description = "행 수", example = "10", required = true)
    private Integer rows;
    
    @NotNull
    @Positive
    @Schema(description = "열 수", example = "15", required = true)
    private Integer columns;
    
    @Schema(description = "좌석 배치 방식", example = "STANDARD", allowableValues = {"STANDARD", "CUSTOM"})
    private SeatLayoutType layoutType = SeatLayoutType.STANDARD;
    
    public enum SeatLayoutType {
        STANDARD,  // 모든 좌석 생성
        CUSTOM     // 향후 확장용 (특정 좌석만 생성)
    }
} 