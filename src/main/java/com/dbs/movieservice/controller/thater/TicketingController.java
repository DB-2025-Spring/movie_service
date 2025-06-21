package com.dbs.movieservice.controller.thater;

import com.dbs.movieservice.domain.member.Card;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.domain.ticketing.Ticket;
import com.dbs.movieservice.repository.member.CustomerRepository;
import com.dbs.movieservice.repository.ticketing.TicketRepository;
import com.dbs.movieservice.service.ticketing.PaymentService;
import com.dbs.movieservice.service.ticketing.TicketService;
import com.dbs.movieservice.service.member.CustomerService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/ticketing")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "티켓팅 API", description = "사용자 티켓팅 관련 API")
public class TicketingController {
    private final TicketService ticketService;
    private final PaymentService paymentService;
    private final CustomerService customerService;
//    String customerInputId = SecurityUtils.getCurrentCustomerInputId();
    //todo DTO로 response

    @PostMapping("/create-temporal-ticket")
    @Operation(
            summary = "티켓 생성",
            description = "로그인한 사용자가 좌석을 선택해 티켓을 생성합니다. 이미 선점된 좌석이 포함된 경우 생성에 실패하며 409 Conflict 응답을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "티켓 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TicketResponseDTO.class))
                    )),
            @ApiResponse(responseCode = "409", description = "좌석이 이미 선점되어 티켓 생성 실패"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (예: 좌석 ID 또는 스케줄 ID가 없음)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> createTickets(@RequestBody TicketCreateRequest request) {
            String customerInputId = SecurityUtils.getCurrentCustomerInputId();
            Customer customer = customerService.getCustomerByInputId(customerInputId);
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
            return ResponseEntity.ok(tickets.stream()
                    .map(ticket -> new TicketResponseDTO(
                            ticket.getTicketId(),
                            ticket.getSeat().getSeatId(),
                            ticket.getAudienceType(),
                            ticket.getBookingDatetime(),
                            ticket.getSchedule().getScheduleId(),
                            ticket.getPayment() != null ? ticket.getPayment().getPaymentId() : null
                    ))
                    .toList());
    }

    /**
     * create-ticket의 response Dto
     */
    @Data
    @AllArgsConstructor
    public static class TicketResponseDTO {
        private Long ticketId;
        private Long seatId;
        private String audienceType;
        private LocalDateTime bookingDatetime;
        private Long scheduleId;
        private Long paymentId;
    }


    @PostMapping("/create-payment")
    @Operation(
            summary = "결제 생성",
            description = "전달받은 티켓들로부터, 결제를 생성. 카드잔액이나, 포인트가 부족할 시 에러를 리턴."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 생성 성공",
                    content = @Content(schema = @Schema(implementation = PaymentResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "결제 실패 (예: 포인트 부족, 결제 금액 음수)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request) {
        try {
            String customerInputId = SecurityUtils.getCurrentCustomerInputId();
            Customer customer = customerService.getCustomerByInputId(customerInputId);

            Card card = new Card();
            card.setCardId(request.getCardId());

            List<Ticket> tickets = ticketService.getTicketsByIds(request.getTicketIds());
            ticketService.validateTicketsOwnership(tickets, customer.getCustomerId());
            Payment payment = paymentService.createPayment(
                    customer,
                    tickets,
                    request.getUsePoint(),
                    request.getDiscountAmount()
            );
            PaymentResponseDTO response = new PaymentResponseDTO(
                    payment.getPaymentId(),
                    payment.getPaymentAmount(),
                    payment.getPaymentMethod(),
                    payment.getApprovalNumber(),
                    payment.getPaymentStatus(),
                    payment.getPaymentDate(),
                    payment.getDiscountAmount(),
                    payment.getUsedPoints(),
                    payment.getPaymentKey()
            );
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * createpayment의 response용 DTO
     */
    @Data
    @AllArgsConstructor
    public static class PaymentResponseDTO {
        private Long paymentId;
        private Integer paymentAmount;
        private String paymentMethod;
        private Integer approvalNumber;
        private String paymentStatus;
        private LocalDate paymentDate;
        private Integer discountAmount;
        private Integer usedPoints;
        private String paymentKey;
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
            Payment cancelledPayment = paymentService.cancelPayment(request.getPaymentId());
            return ResponseEntity.ok("cancelledPayment");
    }

    @PostMapping("/delete-temporary-tickets")
    @Operation(
            summary = "임시 티켓 삭제",
            description = "결제 전에 생성된 티켓들을 삭제합니다. 해당 좌석은 다시 사용 가능해집니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "임시 티켓 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "요청한 티켓 중 일부가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> deleteTemporaryTickets(@RequestBody DeleteTicketsRequest request) {
        try {
            ticketService.deleteTicketsBeforePay(request.getTicketIds());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/paymentTest")
    public ResponseEntity<?> paymentTest() {
        Customer customer = new Customer();
        customer.setCustomerId(1L);
        List<Payment> payments= paymentService.getAllPaymentByCustomer(customer);

        System.out.println(payments);
        System.out.println("////////");
        for(Payment payment:payments){
            System.out.println("////");
            System.out.println(payment.getPaymentId());
            List<Ticket> tickets = payment.getTickets();
            for(Ticket ticket:tickets){
                System.out.println("////ticket");
                System.out.println(ticket.getTicketId());
                System.out.println(ticket.getSchedule().getMovie().getMovieName());
            }
        }
       return ResponseEntity.ok("good");
    }

    @GetMapping("/my-bookings")
    @Operation(
            summary = "나의 예매내역 조회",
            description = "로그인한 사용자의 예매내역을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예매내역 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        try {
            String customerInputId = SecurityUtils.getCurrentCustomerInputId();
            Customer customer = customerService.getCustomerByInputId(customerInputId);
            
            List<Payment> payments = paymentService.getAllPaymentByCustomer(customer);
            
            List<BookingResponse> bookings = payments.stream()
                    .flatMap(payment -> payment.getTickets().stream()
                            .map(ticket -> BookingResponse.builder()
                                    .bookingId(payment.getPaymentId())
                                    .movieTitle(ticket.getSchedule().getMovie().getMovieName())
                                    .showDate(ticket.getSchedule().getScheduleDate().toString())
                                    .showTime(ticket.getSchedule().getScheduleStartTime().toLocalTime().toString())
                                    .theater(ticket.getSchedule().getTheater().getTheaterName())
                                    .seatNumber(ticket.getSeat().getRowNumber() + "-" + ticket.getSeat().getColumnNumber())
                                    .totalPrice(payment.getPaymentAmount())
                                    .paymentMethod(payment.getPaymentMethod())
                                    .status("예매완료") // paymentCancel 필드가 없으므로 기본값
                                    .bookingDate(payment.getPaymentDate().toString())
                                    .bookingNumber("TKT" + payment.getPaymentId())
                                    .build()))
                    .toList();
                    
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("예매내역 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/watched-movies")
    @Operation(
            summary = "관람 완료한 영화 목록 조회 (무비로그)",
            description = "로그인한 사용자가 관람 완료한 영화 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관람 완료 영화 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<WatchedMovieResponse>> getWatchedMovies() {
        try {
            String customerInputId = SecurityUtils.getCurrentCustomerInputId();
            Customer customer = customerService.getCustomerByInputId(customerInputId);
            
            List<Payment> payments = paymentService.getAllPaymentByCustomer(customer);
            
            // 상영일이 현재보다 이전인 것들만 필터링 (관람 완료)
            List<WatchedMovieResponse> watchedMovies = payments.stream()
                    .flatMap(payment -> payment.getTickets().stream())
                    .filter(ticket -> ticket.getSchedule().getScheduleDate().isBefore(java.time.LocalDate.now())) // 상영일이 지난 것만
                    .map(ticket -> WatchedMovieResponse.builder()
                            .movieId(ticket.getSchedule().getMovie().getMovieId())
                            .title(ticket.getSchedule().getMovie().getMovieName())
                            .poster("/placeholder.svg") // TODO: 실제 포스터 URL 연결
                            .watchDate(ticket.getSchedule().getScheduleDate().toString())
                            .theater(ticket.getSchedule().getTheater().getTheaterName())
                            .genre("액션") // TODO: 실제 장르 정보 연결
                            .build())
                    .distinct() // 중복 제거
                    .toList();
                    
            return ResponseEntity.ok(watchedMovies);
        } catch (Exception e) {
            log.error("관람 완료 영화 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> tempFunction(){
        System.out.println("호출됨");
        return ResponseEntity.ok("test");
    }

    /**
     * 티켓 생성용 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketCreateRequest {
        private Long scheduleId;
        private List<Long> seatIds;
        private int adultNumber;
    }

    /**
     * 결제 생성용 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequest {
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

    /**
     * 예매취소가 아닌, 결제 취소 요청을 위한 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteTicketsRequest {
        private List<Long> ticketIds;
    }

    /**
     * 예매내역 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingResponse {
        private Long bookingId;
        private String movieTitle;
        private String showDate;
        private String showTime;
        private String theater;
        private String seatNumber;
        private Integer totalPrice;
        private String paymentMethod;
        private String status;
        private String bookingDate;
        private String bookingNumber;
    }

    /**
     * 관람 완료 영화 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WatchedMovieResponse {
        private Long movieId;
        private String title;
        private String poster;
        private String watchDate;
        private String theater;
        private String genre;
    }
}
