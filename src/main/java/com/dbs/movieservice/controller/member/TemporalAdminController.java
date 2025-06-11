package com.dbs.movieservice.controller.member;

import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.service.member.ClientLevelService;
import com.dbs.movieservice.service.member.IssueCouponService;
import com.dbs.movieservice.service.movie.ActorService;
import com.dbs.movieservice.service.movie.GenreService;
import com.dbs.movieservice.service.movie.MovieGenreService;
import com.dbs.movieservice.service.movie.MovieService;
import com.dbs.movieservice.service.theater.ScheduleService;
import com.dbs.movieservice.service.theater.SeatService;
import com.dbs.movieservice.service.theater.TheaterService;
import com.dbs.movieservice.service.ticketing.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @deprecated 이 컨트롤러는 AdminController로 통합되었습니다.
 * AdminController의 /admin/* 엔드포인트를 사용하세요.
 */
@RestController
@RequestMapping("/admin1")
@Tag(name = "임시관리자 API (사용 중지)", description = "AdminController로 통합되었습니다.")
@Deprecated
public class TemporalAdminController {
//<<<<<<< HEAD
//    private final MovieService movieService;
//    private final ActorService actorService;
//    private final GenreService genreService;
//    private final TheaterService theaterService;
//    private final SeatService seatService;
//    private final ScheduleService scheduleService;
//    private final CouponService couponService;
//    private final ClientLevelService clientLevelService;
//    private final IssueCouponService issueCouponService;
//    private final MovieGenreService movieGenreService;
//
//
//    @PostMapping("/create-theater")
//    @Operation(summary = "상영관 생성", description = "새로운 상영관(Theater)을 생성합니다.")
//    public ResponseEntity<Theater> createTheater(@Valid @RequestBody CreateTheaterRequest request) {
//        Theater createTheater = theaterService.createTheater(request.getTheaterName());
//        return ResponseEntity.ok(createTheater);
//    }
//
//    @PostMapping("/create-seat")
//    @Operation(summary = "좌석 생성", description = "지정된 상영관에 좌석을 생성합니다.")
//    public ResponseEntity<Seat> createSeat(@Valid @RequestBody CreateSeatRequest request) {
//        Theater theater = new Theater();
//        theater.setTheaterId(request.getTheaterId());
//
//        Seat seat = new Seat();
//        seat.setRowNumber(request.getSeatRow());
//        seat.setColumnNumber(request.getSeatCol());
//        seat.setTheater(theater);
//
//        Seat createdSeat = seatService.createSeat(seat);
//        return ResponseEntity.ok(createdSeat);
//    }
//
//    @PostMapping("/create-schedule")
//    @Operation(summary = "상영 일정 생성", description = "지정된 날짜와, 상영관, 영화에 맞는 일정을 생성합니다.")
//    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody CreateScheduleRequest request) {
//        Schedule schedule = scheduleService.createSchedule(request.getTheaterId(),request.getMovieId(),
//                request.getScheduleStartTime());
//        return ResponseEntity.ok(schedule);
//    }
//
//    @PostMapping("/movie-genre")
//    @Operation(summary ="영화장르 생성기", description="영화id와 genreid를 받아 생성")
//    public ResponseEntity<?> createMovieGenre(@RequestBody CreateMovieGenreRequest request) {
//        movieGenreService.createMovieGenre(request.getMovieId(), request.getGenreId());
//        return ResponseEntity.ok().build();
//    }
//
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class CreateMovieGenreRequest {
//        Long MovieId;
//        Long GenreId;
//    }
//
//    /**
//     * createTheater 전용DTO
//     */
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class CreateTheaterRequest {
//        @NotNull(message = "상영관 이름은 필수입니다")
//        private String TheaterName;
//    }
//
//    /**
//     * createSeat 전용 DTO
//     */
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class CreateSeatRequest {
//
//        @NotNull(message = "좌석 행 번호는 필수입니다.")
//        private Integer seatRow;
//
//        @NotNull(message = "좌석 열 번호는 필수입니다.")
//        private Integer seatCol;
//
//        @NotNull(message = "상영관 ID는 필수입니다.")
//        private Long theaterId;
//    }
//
//    /**
//     * createSchedule 전용 DTO
//     */
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class CreateScheduleRequest {
//
//        @NotNull(message = "상영관 ID는 필수입니다.")
//        private Long theaterId;
//
//        @NotNull(message = "영화 ID는 필수입니다.")
//        private Long movieId;
//
//        @NotNull(message = "시작 시간은 필수입니다.")
//        @Future(message = "시작 시간은 현재보다 이후여야 합니다.")
//        private LocalDateTime scheduleStartTime;
//    }
//=======
//
//    // 모든 기능이 AdminController로 통합되었습니다.
//    // AdminController의 다음 엔드포인트들을 사용하세요:
//    //
//    // 상영관 관리:
//    // GET    /admin/theaters           - 상영관 조회
//    // POST   /admin/theaters           - 상영관 생성
//    // PUT    /admin/theaters/{id}      - 상영관 수정
//    // DELETE /admin/theaters/{id}      - 상영관 삭제
//    //
//    // 좌석 관리:
//    // GET    /admin/seats              - 좌석 조회
//    // POST   /admin/seats              - 좌석 생성
//    // POST   /admin/seats/bulk         - 좌석 일괄 생성
//    // PUT    /admin/seats/{id}         - 좌석 수정
//    // DELETE /admin/seats/{id}         - 좌석 삭제
//    //
//    // 상영일정 관리:
//    // GET    /admin/schedules          - 상영일정 조회
//    // POST   /admin/schedules          - 상영일정 생성 (간단)
//    // POST   /admin/schedules/advanced - 상영일정 생성 (고급)
//    // PUT    /admin/schedules/{id}     - 상영일정 수정
//    // DELETE /admin/schedules/{id}     - 상영일정 삭제
}
