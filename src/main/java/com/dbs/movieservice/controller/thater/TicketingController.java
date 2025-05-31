package com.dbs.movieservice.controller.thater;

import com.dbs.movieservice.domain.member.Card;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.domain.ticketing.Ticket;
import com.dbs.movieservice.repository.ticketing.TicketRepository;
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
    private final TicketRepository ticketRepository;

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
    public ResponseEntity<?> createTickets(@RequestBody TicketCreateRequest request) {

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
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 예약된 좌석입니다."); // 좌석 중복 등 실패
            }
            return ResponseEntity.ok(tickets);
    }

    @PostMapping("/create-payment")
    @Operation(
            summary = "결제 생성",
            description = "전달받은 티켓들로부터, 결제를 생성. 카드잔액이나, 포인트가 부족할 시 에러를 리턴."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 생성 성공",
                    content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "400", description = "결제 실패 (예: 포인트 부족, 결제 금액 음수)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request) {
        try {
            Customer customer = new Customer();
            customer.setCustomerId(request.getCustomerId());

            Card card = new Card();
            card.setCardId(request.getCardId());

            List<Ticket> tickets = ticketService.getTicketsByIds(request.getTicketIds());
            ticketService.validateTicketsOwnership(tickets, customer.getCustomerId());
            Payment payment = paymentService.createPayment(
                    customer,
                    tickets,
                    card,
                    request.getUsePoint(),
                    request.getDiscountAmount()
            );

            return ResponseEntity.ok(payment);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
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

    @PostMapping("/cancel-payment")
    @Operation(
            summary = "결제 취소",
            description = "결제 ID를 통해 결제를 취소하고 관련 티켓도 삭제합니다. 결제가 존재하지 않거나 이미 취소된 경우 에러를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 취소 성공",
                    content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "400", description = "결제 취소 실패 (예: 결제 없음 또는 잘못된 ID)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> cancelPayment(@RequestBody CancelPaymentRequest request) {
        try {
            Payment payment = new Payment();
            payment.setPaymentId(request.getPaymentId());

            Payment cancelled = paymentService.cancelPayment(payment);
            return ResponseEntity.ok(cancelled);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * 결제 생성용 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequest {
        private Long customerId;
        private List<Long> ticketIds;
        private Long cardId;
        private int usePoint;
        private int discountAmount;
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        @Schema(description = "에러 메시지", example = "결제 금액이 0보다 작을 수 없습니다.")
        private String message;
    }

    /**
     * 결제 취소 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelPaymentRequest {
        @Schema(description = "취소할 결제 ID", example = "123")
        private Long paymentId;
    }
}
