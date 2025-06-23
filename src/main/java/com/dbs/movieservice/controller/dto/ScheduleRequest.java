package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
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


    @Schema(hidden = true)
    private LocalDate scheduleDate;


    @Schema(hidden = true)
    private Integer scheduleSequence;
    
    @NotNull
    @Future(message = "시작 시간은 현재보다 이후여야 합니다.")
    @Schema(description = "상영시작시간", example = "2024-04-26T14:30:00", required = true)
    private LocalDateTime scheduleStartTime;

    @Future(message = "마감 시간은 현재보다 이후여야 합니다.")
    @Schema(description = "상영마감시간", example = "2024-04-27T14:30:00", required = false)
    private LocalDateTime scheduleEndTime;
}