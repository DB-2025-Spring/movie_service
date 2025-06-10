package com.dbs.movieservice.controller.member;

import com.dbs.movieservice.controller.dto.*;
import com.dbs.movieservice.domain.member.ClientLevel;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.movie.Actor;
import com.dbs.movieservice.domain.movie.Genre;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.domain.ticketing.Coupon;
import com.dbs.movieservice.dto.GenreDto;
import com.dbs.movieservice.service.member.ClientLevelService;
import com.dbs.movieservice.service.member.CustomerService;
import com.dbs.movieservice.service.movie.ActorService;
import com.dbs.movieservice.service.movie.GenreService;
import com.dbs.movieservice.service.movie.MovieService;
import com.dbs.movieservice.service.theater.ScheduleService;
import com.dbs.movieservice.service.theater.SeatService;
import com.dbs.movieservice.service.theater.TheaterService;
import com.dbs.movieservice.service.ticketing.CouponService;
import com.dbs.movieservice.service.member.IssueCouponService;
import com.dbs.movieservice.controller.dto.IssuedCouponResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "관리자 API", description = "시스템 관리자용 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final MovieService movieService;
    private final ActorService actorService;
    private final GenreService genreService;
    private final TheaterService theaterService;
    private final SeatService seatService;
    private final ScheduleService scheduleService;
    private final CouponService couponService;
    private final ClientLevelService clientLevelService;
    private final IssueCouponService issueCouponService;
    private final CustomerService customerService;

    // 영화 관리
    
    @GetMapping("/movies")
    @Operation(summary = "모든 영화 조회", description = "시스템에 등록된 모든 영화 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = Movie.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.findAllMovies();
        return ResponseEntity.ok(movies);
    }

    @PostMapping("/movies")
    @Operation(summary = "영화 생성", description = "새로운 영화를 시스템에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = Movie.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody MovieRequest request) {
        Movie movie = new Movie();
        movie.setViewRating(request.getViewRating());
        movie.setMovieName(request.getMovieName());
        movie.setRunningTime(request.getRunningTime());
        movie.setDirectorName(request.getDirectorName());
        movie.setMovieDesc(request.getMovieDesc());
        movie.setDistributor(request.getDistributor());
        movie.setImageUrl(request.getImageUrl());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setEndDate(request.getEndDate());
        movie.setCoo(request.getCoo());
        
        Movie savedMovie = movieService.saveMovie(movie);
        return ResponseEntity.ok(savedMovie);
    }

    @PutMapping("/movies/{movieId}")
    @Operation(summary = "영화 수정", description = "기존 영화 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = Movie.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "영화를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Movie> updateMovie(
            @Parameter(description = "영화 ID", example = "1", required = true) @PathVariable Long movieId, 
            @Valid @RequestBody MovieRequest request) {
        Movie updatedMovie = movieService.updateMovie(
            movieId, request.getViewRating(), request.getMovieName(),
            request.getRunningTime(), request.getDirectorName(), request.getMovieDesc(),
            request.getDistributor(), request.getImageUrl(), request.getReleaseDate(),
            request.getEndDate(), request.getCoo()
        );
        return ResponseEntity.ok(updatedMovie);
    }

    @DeleteMapping("/movies/{movieId}")
    @Operation(summary = "영화 삭제", description = "영화를 시스템에서 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "영화를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> deleteMovie(
            @Parameter(description = "영화 ID", example = "1", required = true) @PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.noContent().build();
    }

    // 배우 관리
    
    @GetMapping("/actors")
    @Operation(summary = "모든 배우 조회", description = "시스템에 등록된 모든 배우 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = Actor.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<Actor>> getAllActors() {
        List<Actor> actors = actorService.findAllActors();
        return ResponseEntity.ok(actors);
    }

    @PostMapping("/actors")
    @Operation(summary = "배우 생성", description = "새로운 배우를 시스템에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = Actor.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Actor> createActor(@Valid @RequestBody ActorRequest request) {
        Actor actor = new Actor();
        actor.setActorName(request.getActorName());
        
        Actor savedActor = actorService.saveActor(actor);
        return ResponseEntity.ok(savedActor);
    }

    @PutMapping("/actors/{actorId}")
    @Operation(summary = "배우 수정", description = "기존 배우 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = Actor.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "배우를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Actor> updateActor(
            @Parameter(description = "배우 ID", example = "1", required = true) @PathVariable Long actorId,
            @Valid @RequestBody ActorRequest request) {
        Actor updatedActor = actorService.updateActor(actorId, request.getActorName());
        return ResponseEntity.ok(updatedActor);
    }

    @DeleteMapping("/actors/{actorId}")
    @Operation(summary = "배우 삭제", description = "배우를 시스템에서 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "배우를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> deleteActor(
            @Parameter(description = "배우 ID", example = "1", required = true) @PathVariable Long actorId) {
        actorService.deleteActor(actorId);
        return ResponseEntity.noContent().build();
    }

    // 장르 관리
    
    @GetMapping("/genres")
    @Operation(summary = "모든 장르 조회", description = "시스템에 등록된 모든 장르 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = Genre.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<GenreDto>> getAllGenres() {
        List<GenreDto> genres = genreService.findAllGenres();
        return ResponseEntity.ok(genres);
    }

    @PostMapping("/genres")
    @Operation(summary = "장르 생성", description = "새로운 장르를 시스템에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = Genre.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Genre> createGenre(@Valid @RequestBody GenreRequest request) {
        Genre genre = new Genre();
        genre.setGenreName(request.getGenreName());
        
        Genre savedGenre = genreService.saveGenre(genre);
        return ResponseEntity.ok(savedGenre);
    }

    @PutMapping("/genres/{genreId}")
    @Operation(summary = "장르 수정", description = "기존 장르 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = Genre.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "장르를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Genre> updateGenre(
            @Parameter(description = "장르 ID", example = "1", required = true) @PathVariable Long genreId,
            @Valid @RequestBody GenreRequest request) {
        Genre updatedGenre = genreService.updateGenre(genreId, request.getGenreName());
        return ResponseEntity.ok(updatedGenre);
    }

    @DeleteMapping("/genres/{genreId}")
    @Operation(summary = "장르 삭제", description = "장르를 시스템에서 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "장르를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> deleteGenre(
            @Parameter(description = "장르 ID", example = "1", required = true) @PathVariable Long genreId) {
        genreService.deleteGenre(genreId);
        return ResponseEntity.noContent().build();
    }

    // 상영관 관리
    
    @GetMapping("/theaters")
    @Operation(summary = "모든 상영관 조회", description = "시스템에 등록된 모든 상영관 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = Theater.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<Theater>> getAllTheaters() {
        List<Theater> theaters = theaterService.findAllTheaters();
        return ResponseEntity.ok(theaters);
    }

    @PostMapping("/theaters")
    @Operation(summary = "상영관 생성", description = "새로운 상영관을 시스템에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = Theater.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Theater> createTheater(@Valid @RequestBody TheaterRequest request) {
        Theater theater = new Theater();
        theater.setTheaterName(request.getTheaterName());
        theater.setTotalSeats(request.getTotalSeats());
        
        Theater savedTheater = theaterService.saveTheater(theater);
        return ResponseEntity.ok(savedTheater);
    }

    @PutMapping("/theaters/{theaterId}")
    @Operation(summary = "상영관 수정", description = "기존 상영관 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = Theater.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "상영관을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Theater> updateTheater(
            @Parameter(description = "상영관 ID", example = "1", required = true) @PathVariable Long theaterId,
            @Valid @RequestBody TheaterRequest request) {
        Theater updatedTheater = theaterService.updateTheater(
            theaterId, request.getTheaterName(), request.getTotalSeats()
        );
        return ResponseEntity.ok(updatedTheater);
    }

    @DeleteMapping("/theaters/{theaterId}")
    @Operation(summary = "상영관 삭제", description = "상영관을 시스템에서 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "상영관을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> deleteTheater(
            @Parameter(description = "상영관 ID", example = "1", required = true) @PathVariable Long theaterId) {
        theaterService.deleteTheater(theaterId);
        return ResponseEntity.noContent().build();
    }

    // 좌석 관리
    
    @GetMapping("/seats")
    @Operation(summary = "모든 좌석 조회", description = "시스템에 등록된 모든 좌석 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = Seat.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<Seat>> getAllSeats() {
        List<Seat> seats = seatService.findAllSeats();
        return ResponseEntity.ok(seats);
    }

    @PostMapping("/seats")
    @Operation(summary = "좌석 생성", description = "새로운 좌석을 시스템에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = Seat.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Seat> createSeat(@Valid @RequestBody SeatRequest request) {
        Seat savedSeat = seatService.saveSeat(
            request.getTheaterId(), request.getRowNumber(), request.getColumnNumber()
        );
        return ResponseEntity.ok(savedSeat);
    }

    @PutMapping("/seats/{seatId}")
    @Operation(summary = "좌석 수정", description = "기존 좌석 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = Seat.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "좌석을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Seat> updateSeat(
            @Parameter(description = "좌석 ID", example = "1", required = true) @PathVariable Long seatId,
            @Valid @RequestBody SeatRequest request) {
        Seat updatedSeat = seatService.updateSeat(
            seatId, request.getTheaterId(), request.getRowNumber(), request.getColumnNumber()
        );
        return ResponseEntity.ok(updatedSeat);
    }

    @DeleteMapping("/seats/{seatId}")
    @Operation(summary = "좌석 삭제", description = "좌석을 시스템에서 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "좌석을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> deleteSeat(
            @Parameter(description = "좌석 ID", example = "1", required = true) @PathVariable Long seatId) {
        seatService.deleteSeat(seatId);
        return ResponseEntity.noContent().build();
    }

    // 상영일정 관리
    
    @GetMapping("/schedules")
    @Operation(summary = "모든 상영일정 조회", description = "시스템에 등록된 모든 상영일정 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = Schedule.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.findAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/schedules")
    @Operation(summary = "상영일정 생성", description = "새로운 상영일정을 시스템에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = Schedule.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody ScheduleRequest request) {
        Schedule savedSchedule = scheduleService.saveSchedule(
            request.getMovieId(), request.getTheaterId(), request.getScheduleDate(),
            request.getScheduleSequence(), request.getScheduleStartTime()
        );
        return ResponseEntity.ok(savedSchedule);
    }

    @PutMapping("/schedules/{scheduleId}")
    @Operation(summary = "상영일정 수정", description = "기존 상영일정 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = Schedule.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "상영일정을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Schedule> updateSchedule(
            @Parameter(description = "상영일정 ID", example = "1", required = true) @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleRequest request) {
        Schedule updatedSchedule = scheduleService.updateSchedule(
            scheduleId, request.getMovieId(), request.getTheaterId(),
            request.getScheduleDate(), request.getScheduleSequence(), request.getScheduleStartTime()
        );
        return ResponseEntity.ok(updatedSchedule);
    }

    @DeleteMapping("/schedules/{scheduleId}")
    @Operation(summary = "상영일정 삭제", description = "상영일정을 시스템에서 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "상영일정을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> deleteSchedule(
            @Parameter(description = "상영일정 ID", example = "1", required = true) @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    // 쿠폰 관리
    
    @GetMapping("/coupons")
    @Operation(summary = "모든 쿠폰 조회", description = "시스템에 등록된 모든 쿠폰 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = Coupon.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.findAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @PostMapping("/coupons")
    @Operation(summary = "쿠폰 생성", description = "새로운 쿠폰을 시스템에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = Coupon.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody CouponRequest request) {
        Coupon savedCoupon = couponService.saveCoupon(
            request.getCouponName(), request.getCouponDescription(),
            request.getStartDate(), request.getEndDate()
        );
        return ResponseEntity.ok(savedCoupon);
    }

    @PutMapping("/coupons/{couponId}")
    @Operation(summary = "쿠폰 수정", description = "기존 쿠폰 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = Coupon.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Coupon> updateCoupon(
            @Parameter(description = "쿠폰 ID", example = "1", required = true) @PathVariable Long couponId,
            @Valid @RequestBody CouponRequest request) {
        Coupon updatedCoupon = couponService.updateCoupon(
            couponId, request.getCouponName(), request.getCouponDescription(),
            request.getStartDate(), request.getEndDate()
        );
        return ResponseEntity.ok(updatedCoupon);
    }

    @DeleteMapping("/coupons/{couponId}")
    @Operation(summary = "쿠폰 삭제", description = "쿠폰을 시스템에서 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> deleteCoupon(
            @Parameter(description = "쿠폰 ID", example = "1", required = true) @PathVariable Long couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }

    // 고객등급 관리
    
    @GetMapping("/client-levels")
    @Operation(summary = "모든 고객등급 조회", description = "시스템에 등록된 모든 고객등급 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = ClientLevel.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<ClientLevel>> getAllClientLevels() {
        List<ClientLevel> levels = clientLevelService.findAllLevels();
        return ResponseEntity.ok(levels);
    }

    @PostMapping("/client-levels")
    @Operation(summary = "고객등급 생성", description = "새로운 고객등급을 시스템에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = ClientLevel.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<ClientLevel> createClientLevel(@Valid @RequestBody ClientLevelRequest request) {
        ClientLevel clientLevel = new ClientLevel(
            request.getLevelId(),
            request.getLevelName(),
            request.getRewardRate()
        );
        
        ClientLevel savedLevel = clientLevelService.saveLevel(clientLevel);
        return ResponseEntity.ok(savedLevel);
    }

    @PutMapping("/client-levels/{levelId}")
    @Operation(summary = "고객등급 수정", description = "기존 고객등급 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = ClientLevel.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "고객등급을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<ClientLevel> updateClientLevel(
            @Parameter(description = "등급 ID", example = "1", required = true) @PathVariable Integer levelId,
            @Valid @RequestBody ClientLevelRequest request) {
        ClientLevel updatedLevel = clientLevelService.updateLevel(
            levelId, request.getLevelName(), request.getRewardRate()
        );
        return ResponseEntity.ok(updatedLevel);
    }

    @DeleteMapping("/client-levels/{levelId}")
    @Operation(summary = "고객등급 삭제", description = "고객등급을 시스템에서 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "고객등급을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> deleteClientLevel(
            @Parameter(description = "등급 ID", example = "1", required = true) @PathVariable Integer levelId) {
        clientLevelService.deleteLevel(levelId);
        return ResponseEntity.noContent().build();
    }

    // 쿠폰 발급 관리

    @PostMapping("/issue-coupon/{customerInputId}/{couponId}")
    @Operation(summary = "특정 고객에게 쿠폰 발급", description = "관리자가 특정 고객에게 특정 쿠폰을 발급합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "쿠폰 발급 성공",
                content = @Content(schema = @Schema(implementation = IssuedCouponResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (이미 발급된 쿠폰 등)"),
        @ApiResponse(responseCode = "404", description = "고객 또는 쿠폰을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<?> issueCouponToCustomer(
            @Parameter(description = "고객 입력 ID", example = "testuser123", required = true) 
            @PathVariable String customerInputId,
            @Parameter(description = "쿠폰 ID", example = "1", required = true) 
            @PathVariable Long couponId) {
        IssuedCouponResponse issuedCoupon = issueCouponService.issueCouponToCustomer(customerInputId, couponId);
        
        log.info("Admin issued coupon {} to customer: {}", couponId, customerInputId);
        return ResponseEntity.status(HttpStatus.CREATED).body(issuedCoupon);
    }

    @DeleteMapping("/revoke-coupon/{customerInputId}/{couponId}")
    @Operation(summary = "특정 고객의 쿠폰 발급 취소", description = "관리자가 특정 고객의 발급된 쿠폰을 취소합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "쿠폰 발급 취소 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (발급되지 않은 쿠폰 등)"),
        @ApiResponse(responseCode = "404", description = "고객 또는 쿠폰을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<?> revokeCouponFromCustomer(
            @Parameter(description = "고객 입력 ID", example = "testuser123", required = true) 
            @PathVariable String customerInputId,
            @Parameter(description = "쿠폰 ID", example = "1", required = true) 
            @PathVariable Long couponId) {
        issueCouponService.revokeCouponFromCustomer(customerInputId, couponId);
        
        log.info("Admin revoked coupon {} from customer: {}", couponId, customerInputId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer-coupons/{customerInputId}")
    @Operation(summary = "특정 고객의 발급된 쿠폰 목록 조회", description = "관리자가 특정 고객에게 발급된 모든 쿠폰 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "쿠폰 목록 조회 성공",
                content = @Content(schema = @Schema(implementation = IssuedCouponResponse.class))),
        @ApiResponse(responseCode = "404", description = "고객을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<?> getCustomerCoupons(
            @Parameter(description = "고객 입력 ID", example = "testuser123", required = true) 
            @PathVariable String customerInputId) {
        List<IssuedCouponResponse> coupons = issueCouponService.getIssuedCouponsByCustomerInputId(customerInputId);
        
        log.info("Admin retrieved {} coupons for customer: {}", coupons.size(), customerInputId);
        return ResponseEntity.ok(coupons);
    }

    // 고객 관리
    
    @GetMapping("/users")
    @Operation(summary = "모든 고객 조회", description = "시스템에 등록된 모든 고객 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<CustomerResponse>> getAllUsers() {
        List<CustomerResponse> users = customerService.findAllCustomers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{customerInputId}")
    @Operation(summary = "사용자 정보 수정", description = "특정 사용자의 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<CustomerResponse> updateUser(
            @Parameter(description = "사용자 ID", example = "testuser123", required = true) 
            @PathVariable String customerInputId,
            @Valid @RequestBody CustomerUpdateRequest request) {
        CustomerResponse updatedUser = customerService.updateCustomer(customerInputId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/users/{customerInputId}/level")
    @Operation(summary = "사용자 등급 직접 변경", description = "관리자가 특정 사용자의 등급을 직접 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "등급 변경 성공",
                content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "사용자 또는 등급을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<CustomerResponse> updateUserLevel(
            @Parameter(description = "사용자 ID", example = "testuser123", required = true) 
            @PathVariable String customerInputId,
            @Parameter(description = "새로운 등급 ID", example = "3", required = true) 
            @RequestParam Integer levelId) {
        CustomerResponse updatedUser = customerService.updateCustomerLevel(customerInputId, levelId);
        return ResponseEntity.ok(updatedUser);
    }
}
