package com.dbs.movieservice.controller.dto;

import com.dbs.movieservice.domain.theater.Schedule;

import java.time.LocalDateTime;

public record ScheduleResponseDto(
        Long scheduleId,
        String movieName,
        String theaterName,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    public static ScheduleResponseDto from(Schedule schedule) {
        return new ScheduleResponseDto(
                schedule.getScheduleId(),
                schedule.getMovie().getMovieName(),
                schedule.getTheater().getTheaterName(),
                schedule.getScheduleStartTime(),
                schedule.getScheduleEndTime()
        );
    }
}
