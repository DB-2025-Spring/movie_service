package com.dbs.movieservice.controller.thater;

import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.service.theater.ScheduleService;
import com.dbs.movieservice.service.theater.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seat-system")
@RequiredArgsConstructor
@Slf4j
public class SeatSystemController {
    private final ScheduleService scheduleService;
    private final SeatService seatService;

    @PostMapping("/parse-seat-ids")
    @Operation(
            summary = "열/행 좌표를 기반으로 좌석 ID 조회",
            description = "상영 스케줄 ID와 좌석의 행/열 정보를 입력받아 해당 좌석의 seatId 목록을 반환합니다."
    )
    public ResponseEntity<List<Long>> parseSeatIds(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "좌석 ID 추출 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ParseSeatRequest.class))
            )
            @RequestBody ParseSeatRequest request){
        Schedule schedule = new Schedule();
        schedule.setScheduleId(request.getScheduleId());

        List<Seat> seatList = request.getSeats().stream().map(cr -> {
            Seat seat = new Seat();
            seat.setColumnNumber(cr.getColumn());
            seat.setRowNumber(cr.getRow());
            return seat;
        }).toList();
        return ResponseEntity.ok(scheduleService.parseSeatBySchedule(schedule, seatList));
    }

    @PostMapping("/parse-seat-cols-rows")
    @Operation(
            summary = "seatId 목록을 기반으로 행/열 좌표 반환",
            description = "seatId 목록을 전달하면 해당 좌석의 row/column 정보를 포함한 리스트를 반환합니다."
    )
    public ResponseEntity<List<ParseSeatRequest.ColRow>> parseSeatColsRows(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "seatId 리스트 요청",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SeatIdListRequest.class))
            )
            @RequestBody SeatIdListRequest request
    ) {
        List<Seat> seats = seatService.praseIdstoColRow(
                request.getSeatIds().stream()
                        .map(id -> {
                            Seat s = new Seat();
                            s.setSeatId(id);
                            return s;
                        }).toList()
        );

        List<ParseSeatRequest.ColRow> colRowList = seats.stream()
                .map(seat -> {
                    ParseSeatRequest.ColRow cr = new ParseSeatRequest.ColRow();
                    cr.setColumn(seat.getColumnNumber());
                    cr.setRow(seat.getRowNumber());
                    return cr;
                }).toList();

        return ResponseEntity.ok(colRowList);
    }



    /**
     * parse-seat-ids의 requestBody를 위한 dto
     */
    @Getter
    @Setter
    public static class ParseSeatRequest {
        private Long scheduleId;
        private List<ColRow> seats;

        @Getter
        @Setter
        public static class ColRow {
            private Integer column;
            private Integer row;
        }
    }

    @Getter
    @Setter
    @Schema(description = "seatId 목록 요청 DTO")
    public static class SeatIdListRequest {
        @Schema(description = "seatId 리스트", example = "[1, 2, 3]")
        private List<Long> seatIds;
    }


}
