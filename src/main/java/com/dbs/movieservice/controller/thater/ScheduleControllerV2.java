package com.dbs.movieservice.controller.thater;

import com.dbs.movieservice.service.theater.ScheduleService;
import com.dbs.movieservice.service.theater.SeatAvailableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/v2/schedules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "스케쥴 API v2", description = "사용자 스케쥴 관련 API")
public class ScheduleControllerV2 {
    private final ScheduleService scheduleService;
    private final SeatAvailableService seatAvailableService;


    @GetMapping("/dates")
    @Operation(
            summary = "15일 간 전체 영화의 상영일 조회",
            description = "오늘부터 15일 동안 상영 스케줄이 존재하는 날짜 목록을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상영 날짜 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LocalDate.class)))),
            @ApiResponse(responseCode = "204", description = "조회된 날짜 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getAllScheduledDatesForNext15Days() {
        List<LocalDate> availableDates = scheduleService.getScheduledDatesForNext15Days();

        if (availableDates.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(availableDates);
    }
}
