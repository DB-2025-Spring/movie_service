package com.dbs.movieservice.controller.thater;

import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.SeatAvailable;
import com.dbs.movieservice.service.theater.ScheduleService;
import com.dbs.movieservice.service.theater.SeatAvailableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "스케쥴 API", description = "사용자 스케쥴 관련 API")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final SeatAvailableService seatAvailableService;


    @GetMapping("/available-schedule")
    @Operation(
            summary = "7일 간의 상영 스케줄 조회",
            description = "지정된 영화 ID에 대해 오늘부터 7일 간의 상영 스케줄을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스케줄 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Schedule.class)))),
            @ApiResponse(responseCode = "204", description = "조회된 스케줄이 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getAvailableSchedules(
            @Parameter(description = "조회할 영화의 ID", required = true)
            @RequestParam Long movieId) {

        List<Schedule> schedules = scheduleService.getSchedulesForNext7Days(movieId);
        if (schedules.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ScheduleDto> scheduleDtos = schedules.stream()
                .map(ScheduleDto::new)
                .toList();
        return ResponseEntity.ok(scheduleDtos);
    }

    @GetMapping("/available-seat-for1day")
    @Operation(
            summary = "선택한 날짜 및 영화의 상영 스케줄별 남은 좌석 수 조회",
            description = "특정 날짜와 영화에 해당하는 모든 상영 스케줄에 대해 사용 가능한 좌석 수를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ScheduleSeatCountResponse.class))
                    )),
            @ApiResponse(responseCode = "204", description = "조회된 스케줄이 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getAvailableSeatCountForDate(
            @Parameter(description = "조회할 날짜 (예: 2025-06-01)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            @Parameter(description = "조회할 영화 ID", required = true)
            @RequestParam Long movieId
    ) {
        Map<Schedule, Long> resultMap = scheduleService.getSchedulesFor1Day(date, movieId);

        if (resultMap.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ScheduleSeatCountDto> resultList = resultMap.entrySet().stream()
                .map(entry -> new ScheduleSeatCountDto(entry.getKey(), entry.getValue()))
                .toList();

        return ResponseEntity.ok(resultList);
    }


    @GetMapping("/{scheduleId}/available-seats")
    @Operation(
            summary = "특정 스케줄의 사용 가능한 좌석 목록 조회",
            description = "scheduleId를 기준으로 해당 스케줄의 사용 가능한 좌석 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SeatAvailableDto.class))
                    )),
            @ApiResponse(responseCode = "404", description = "해당 스케줄이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<SeatAvailableDto>> getAvailableSeats(
            @Parameter(description = "조회할 스케줄 ID", required = true)
            @PathVariable Long scheduleId
    ) {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(scheduleId);

        List<SeatAvailable> availableSeats = seatAvailableService.getAvailableSeatForSchedule(schedule);

        List<SeatAvailableDto> response = availableSeats.stream()
                .map(seat -> new SeatAvailableDto(
                        seat.getSeat().getSeatId(),
                        seat.getSeat().getRowNumber(),
                        seat.getSeat().getColumnNumber(),
                        seat.getIsBooked()  // 문자열일 경우 boolean으로 변환
                ))
                .toList();
        return ResponseEntity.ok(response);
    }






    /**
     * 이 아래로 DTO
     */

    /**
     * getAvailableSeatCountForDate 함수를 위한 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleSeatCountResponse {
        private Schedule schedule;
        private Long availableSeatCount;
    }

    /**
     *
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatAvailableDto {
        private Long seatId;
        private Integer rowNumber;
        private Integer columnNumber;
        private String isBooked;
    }

    /**
     * 스케쥴 조회용 dto
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public class ScheduleDto {
        private Long scheduleId;
        private Long movieId;
        private Long theaterId;
        private LocalDate scheduleDate;
        private Integer scheduleSequence;
        private LocalDateTime scheduleStartTime;
        private LocalDateTime scheduleEndTime;

        public ScheduleDto(Schedule schedule) {
            this.scheduleId = schedule.getScheduleId();
            this.movieId = schedule.getMovie().getMovieId();
            this.theaterId = schedule.getTheater().getTheaterId();
            this.scheduleDate = schedule.getScheduleDate();
            this.scheduleSequence = schedule.getScheduleSequence();
            this.scheduleStartTime = schedule.getScheduleStartTime();
            this.scheduleEndTime = schedule.getScheduleEndTime();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public class ScheduleSeatCountDto {
        private Long scheduleId;
        private Long movieId;
        private Long theaterId;
        private LocalDateTime scheduleStartTime;
        private LocalDateTime scheduleEndTime;
        private Integer scheduleSequence;
        private Long availableSeatCount;

        public ScheduleSeatCountDto(Schedule schedule, Long availableSeatCount) {
            this.scheduleId = schedule.getScheduleId();
            this.movieId = schedule.getMovie().getMovieId();          // Lazy 접근
            this.theaterId = schedule.getTheater().getTheaterId();    // Lazy 접근
            this.scheduleStartTime = schedule.getScheduleStartTime();
            this.scheduleEndTime = schedule.getScheduleEndTime();
            this.scheduleSequence = schedule.getScheduleSequence();
            this.availableSeatCount = availableSeatCount;
        }
    }
}
