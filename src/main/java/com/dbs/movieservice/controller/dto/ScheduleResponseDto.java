package com.dbs.movieservice.controller.dto;

import com.dbs.movieservice.domain.theater.Schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ScheduleResponseDto(
        Long scheduleId,
        String movieName,
        String theaterName,
        LocalDate scheduleDate,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    public static ScheduleResponseDto from(Schedule schedule) {
        return new ScheduleResponseDto(
                schedule.getScheduleId(),
                schedule.getMovie().getMovieName(),
                schedule.getTheater().getTheaterName(),
                schedule.getScheduleDate(),
                schedule.getScheduleStartTime(),
                schedule.getScheduleEndTime()
        );
    }
}
