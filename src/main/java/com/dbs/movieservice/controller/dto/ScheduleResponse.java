package com.dbs.movieservice.controller.dto;

import com.dbs.movieservice.domain.theater.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    private Long scheduleId;
    private Long movieId;
    private String movieTitle;
    private String movieDescription;
    private Integer runningTime;
    private Long theaterId;
    private String theaterName;
    private Integer totalSeats;
    private LocalDate scheduleDate;
    private Integer scheduleSequence;
    private LocalDateTime scheduleStartTime;
    private LocalDateTime scheduleEndTime;

    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
            schedule.getScheduleId(),
            schedule.getMovie().getMovieId(),
            schedule.getMovie().getMovieName(),
            schedule.getMovie().getMovieDesc(),
            schedule.getMovie().getRunningTime(),
            schedule.getTheater().getTheaterId(),
            schedule.getTheater().getTheaterName(),
            schedule.getTheater().getTotalSeats(),
            schedule.getScheduleDate(),
            schedule.getScheduleSequence(),
            schedule.getScheduleStartTime(),
            schedule.getScheduleEndTime()
        );
    }
} 