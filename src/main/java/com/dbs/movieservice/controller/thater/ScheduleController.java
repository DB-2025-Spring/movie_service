package com.dbs.movieservice.controller.thater;

import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.service.theater.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/available-schedule")
    @Operation(
            summary = "7일 간의 상영 스케줄 조회",
            description = "지정된 영화 ID에 대해 오늘부터 7일 간의 상영 스케줄을 반환합니다."
    )
    public List<Schedule> getAvailableSchedules(
            @Parameter(description = "조회할 영화의 ID", required = true)
            @RequestParam Long movieId) {
        return scheduleService.getSchedulesForNext7Days(movieId);
    }

    @GetMapping("/available-seat-for1day")
    @Operation(
            summary = "선택한 날짜 및 영화의 상영 스케줄별 남은 좌석 수 조회",
            description = "특정 날짜와 영화에 해당하는 모든 상영 스케줄에 대해 사용 가능한 좌석 수를 조회합니다."
    )
    public Map<Long, Long> getAvailableSeatCountForDate(
            @Parameter(description = "조회할 날짜 (예: 2025-06-01)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            @Parameter(description = "조회할 영화 ID", required = true)
            @RequestParam Long movieId
    ) {
        return scheduleService.getSchedulesFor1Day(date, movieId);
    }
}
