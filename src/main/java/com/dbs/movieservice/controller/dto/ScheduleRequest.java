package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상영일정 정보 요청 DTO")
public class ScheduleRequest {
    
    @NotNull
    @Positive
    @Schema(description = "영화 ID", example = "1", required = true)
    private Long movieId;
    
    @NotNull
    @Positive
    @Schema(description = "상영관 ID", example = "1", required = true)
    private Long theaterId;
    
    @NotNull
    @Schema(description = "상영일", example = "2024-04-26", required = true)
    private LocalDate scheduleDate;
    
    @NotNull
    @Positive
    @Schema(description = "상영회차", example = "1", required = true)
    private Integer scheduleSequence;
    
    @NotNull
    @Schema(description = "상영시작시간", example = "2024-04-26T14:30:00", required = true)
    private LocalDateTime scheduleStartTime;
} 