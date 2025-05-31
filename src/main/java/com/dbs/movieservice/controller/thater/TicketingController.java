package com.dbs.movieservice.controller.thater;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.ticketing.Ticket;
import com.dbs.movieservice.service.ticketing.PaymentService;
import com.dbs.movieservice.service.ticketing.TicketService;
import com.dbs.movieservice.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ticketing")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "티켓팅 API", description = "사용자 티켓팅 관련 API")
public class TicketingController {
    private final TicketService ticketService;
    private final PaymentService paymentService;

//    String customerInputId = SecurityUtils.getCurrentCustomerInputId();


    @PostMapping("/create-temporal-ticket")
    @Operation(
            summary = "티켓 생성",
            description = "로그인한 사용자가 좌석을 선택해 티켓을 생성합니다. 이미 선점된 좌석이 포함된 경우 생성에 실패하며 409 Conflict 응답을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "티켓 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Ticket.class))
                    )),
            @ApiResponse(responseCode = "409", description = "좌석이 이미 선점되어 티켓 생성 실패"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (예: 좌석 ID 또는 스케줄 ID가 없음)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<Ticket>> createTickets(@RequestBody TicketCreateRequest request) {

            Customer customer = new Customer();
            customer.setCustomerId(request.getCustomerId());
            Schedule schedule = new Schedule();
            schedule.setScheduleId(request.getScheduleId());
            List<Seat> seats = request.getSeatIds().stream().map(seatId->{
                Seat seat = new Seat();
                seat.setSeatId(seatId);
                return seat;
            }).toList();

            List<Ticket> tickets = ticketService.createTicketForCustomer(customer,schedule,seats, request.adultNumber);

            if (tickets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(List.of()); // 좌석 중복 등 실패
            }
            return ResponseEntity.ok(tickets);
        }


    /**
     * 티켓 생성용 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketCreateRequest {
        private Long customerId;
        private Long scheduleId;
        private List<Long> seatIds;
        private int adultNumber;
    }
}
