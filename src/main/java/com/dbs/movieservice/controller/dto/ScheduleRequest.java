package com.dbs.movieservice.controller.dto;

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
public class ScheduleRequest {
    
    @NotNull
    @Positive
    private Long movieId;
    
    @NotNull
    @Positive
    private Long theaterId;
    
    @NotNull
    private LocalDate scheduleDate;
    
    @NotNull
    @Positive
    private Integer scheduleSequence;
    
    @NotNull
    private LocalDateTime scheduleStartTime;
} 