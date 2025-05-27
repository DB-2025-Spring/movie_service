package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "좌석 정보 요청 DTO")
public class SeatRequest {
    
    @NotNull
    @Positive
    @Schema(description = "상영관 ID", example = "1", required = true)
    private Long theaterId;
    
    @NotNull
    @Positive
    @Schema(description = "행 번호", example = "5", required = true)
    private Integer rowNumber;
    
    @NotNull
    @Positive
    @Schema(description = "열 번호", example = "10", required = true)
    private Integer columnNumber;
} 