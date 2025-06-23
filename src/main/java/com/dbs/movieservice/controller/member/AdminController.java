package com.dbs.movieservice.controller.member;

import com.dbs.movieservice.controller.dto.*;
import com.dbs.movieservice.controller.dto.SeatBulkCreateRequest;
import com.dbs.movieservice.domain.member.ClientLevel;
import com.dbs.movieservice.domain.movie.Actor;
import com.dbs.movieservice.domain.movie.Genre;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.domain.ticketing.Coupon;
import com.dbs.movieservice.dto.ActorDto;
import com.dbs.movieservice.dto.GenreDto;
import com.dbs.movieservice.dto.MovieDto;
import com.dbs.movieservice.service.member.ClientLevelService;
import com.dbs.movieservice.service.member.CustomerService;
import com.dbs.movieservice.service.movie.*;
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
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final MovieGenreService movieGenreService;
    private final MovieActorService movieActorService;
    // 영화 관리

    @GetMapping("/movies")
    @Operation(summary = "모든 영화 조회", description = "시스템에 등록된 모든 영화 목록을 조회합니다.")
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        List<Movie> movies = movieService.findAllMovies();
        List<MovieDto> dtos = movies.stream().map((Movie movie) -> new MovieDto(movie)).toList();
        return ResponseEntity.ok(dtos);
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
    public ResponseEntity<MovieDto> updateMovie(
            @Parameter(description = "영화 ID", example = "1", required = true) @PathVariable Long movieId, 
            @Valid @RequestBody MovieRequest request) {
        Movie updatedMovie = movieService.updateMovie(
                movieId,
                request.getViewRating(),
                request.getMovieName(),
                request.getRunningTime(),
                request.getDirectorName(),
                request.getMovieDesc(),
                request.getDistributor(),
                request.getImageUrl(),
                request.getReleaseDate(),
                request.getEndDate(),
                request.getCoo()
        );

        MovieDto movieDto = new MovieDto(updatedMovie);
        return ResponseEntity.ok(movieDto);

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

    @PutMapping("/movie-genre/{movieId}")
    @Operation(summary = "영화의 장르 수정", description = "영화 ID에 해당하는 기존 장르를 제거하고 새로운 장르 ID들로 대체합니다.")
    public ResponseEntity<?> updateMovieGenres(
            @PathVariable Long movieId,
            @RequestBody CreateMovieGenreRequest request
    ) {
        // 1. 기존 장르 연관 삭제
        movieGenreService.deleteAllGenresByMovieId(movieId);

        // 2. 새 장르 등록
        for (Long genreId : request.getMovieGenres()) {
            movieGenreService.createMovieGenre(movieId, genreId);
        }

        return ResponseEntity.ok().build();
    }
    @PutMapping("/movie-actor/{movieId}")
    @Operation(summary = "영화의 배우 수정", description = "영화 ID에 해당하는 기존 배우들을 제거하고 새로운 배우 ID들로 대체합니다.")
    public ResponseEntity<?> updateMovieActors(
            @PathVariable Long movieId,
            @RequestBody CreateMovieActorRequest request
    ) {
        // 1. 기존 배우 연관 삭제
        movieActorService.deleteAllActorsByMovieId(movieId);

        // 2. 새 배우 등록
        for (Long actorId : request.getMovieActors()) {
            movieActorService.createMovieActor(movieId, actorId);
        }

        return ResponseEntity.ok().build();
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
    public ResponseEntity<List<ActorDto>> getAllActors() {
        List<ActorDto> actorDtos = actorService.findAllActors().stream()
                .map(ActorDto::new)
                .toList();
        return ResponseEntity.ok(actorDtos);
    }

    @PostMapping("/actors")
    @Operation(summary = "배우 생성", description = "새로운 배우를 시스템에 등록합니다. 같은 이름의 배우가 있으면 생년월일을 비교하여 처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공 또는 기존 배우 반환",
                content = @Content(schema = @Schema(implementation = Actor.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Actor> createActor(@Valid @RequestBody ActorRequest request) {
        // 이름으로 검색 후 생성 또는 기존 배우 반환
        Actor savedActor = actorService.createOrUpdateActor(request.getActorName(), request.getBirthDate());
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
        Actor updatedActor = actorService.updateActor(actorId, request.getActorName(), 
            request.getBirthDate());
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
        List<Theater> theaters = theaterService.getAllTheaters();
        return ResponseEntity.ok(theaters);
    }

    @PostMapping("/theaters")
    @Operation(summary = "상영관과 좌석 생성", description = "새로운 상영관과 지정된 개수의 좌석을 함께 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = Theater.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Theater> createTheaterWithSeats(@Valid @RequestBody CreateTheaterRequest request) {
        Theater createdTheater = theaterService.createTheaterWithLimitedSeats(
            request.getTheaterName(), request.getRows(), request.getColumns(), request.getTotalSeats()
        );
        return ResponseEntity.ok(createdTheater);
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
            @PathVariable Long theaterId,
            @Valid @RequestBody TheaterUpdateRequest request
    ) {
        Theater updated = theaterService.updateTheater(
                theaterId,
                request.getTheaterName(),
                request.getRows(),
                request.getColumns()
        );
        return ResponseEntity.ok(updated);
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

    @PutMapping("/theaters/{theaterId}/recalculate-seats")
    @Operation(summary = "상영관 좌석 수 재계산", description = "상영관의 실제 좌석 수를 재계산하여 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "재계산 성공",
                content = @Content(schema = @Schema(implementation = Theater.class))),
        @ApiResponse(responseCode = "404", description = "상영관을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Theater> recalculateTheaterSeats(
            @Parameter(description = "상영관 ID", example = "1", required = true) @PathVariable Long theaterId) {
        Theater updatedTheater = theaterService.recalculateTotalSeats(theaterId);
        return ResponseEntity.ok(updatedTheater);
    }

    // 좌석 관리
    
    @GetMapping("/seats")
    @Operation(summary = "모든 좌석 조회", description = "시스템에 등록된 모든 좌석 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = SeatDto.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<SeatDto>> getAllSeats() {
        List<Seat> seats = seatService.findAllSeats();
        List<SeatDto> seatDtos = seats.stream()
                .map(SeatDto::from)
                .toList();
        return ResponseEntity.ok(seatDtos);
    }

    @PostMapping("/seats")
    @Operation(summary = "좌석 생성 (개별)", description = "새로운 좌석을 시스템에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = SeatDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<SeatDto> createSingleSeat(@Valid @RequestBody SeatRequest request) {
        Seat savedSeat = seatService.saveSeat(
            request.getTheaterId(), request.getRowNumber(), request.getColumnNumber()
        );
        return ResponseEntity.ok(SeatDto.from(savedSeat));
    }

    @PostMapping("/seats/bulk")
    @Operation(summary = "좌석 일괄 생성", description = "특정 상영관에 여러 좌석을 한 번에 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = SeatDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<SeatDto>> createSeatsInBulk(@Valid @RequestBody SeatBulkCreateRequest request) {
        List<Seat> savedSeats = seatService.createSeatsInBulk(request.getTheaterId(), request.getSeats());
        List<SeatDto> seatDtos = savedSeats.stream()
                .map(SeatDto::from)
                .toList();
        return ResponseEntity.ok(seatDtos);
    }

    @PutMapping("/seats/{seatId}")
    @Operation(summary = "좌석 수정", description = "기존 좌석 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공",
                content = @Content(schema = @Schema(implementation = SeatDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "좌석을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<SeatDto> updateSeat(
            @Parameter(description = "좌석 ID", example = "1", required = true) @PathVariable Long seatId,
            @Valid @RequestBody SeatRequest request) {
        Seat updatedSeat = seatService.updateSeat(
            seatId, request.getTheaterId(), request.getRowNumber(), request.getColumnNumber()
        );
        return ResponseEntity.ok(SeatDto.from(updatedSeat));
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

    @GetMapping("/theaters/{theaterId}/seats")
    @Operation(summary = "특정 상영관의 모든 좌석 조회", description = "특정 상영관에 속한 모든 좌석을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = SeatDto.class))),
        @ApiResponse(responseCode = "404", description = "상영관을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<SeatDto>> getTheaterSeats(
            @Parameter(description = "상영관 ID", example = "1", required = true) @PathVariable Long theaterId) {
        List<Seat> seats = seatService.findSeatsByTheaterId(theaterId);
        List<SeatDto> seatDtos = seats.stream()
                .map(SeatDto::from)
                .toList();
        return ResponseEntity.ok(seatDtos);
    }

    @DeleteMapping("/theaters/{theaterId}/seats")
    @Operation(summary = "특정 상영관의 모든 좌석 삭제", description = "특정 상영관에 속한 모든 좌석을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "상영관을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> deleteAllTheaterSeats(
            @Parameter(description = "상영관 ID", example = "1", required = true) @PathVariable Long theaterId) {
        seatService.deleteAllSeatsByTheaterId(theaterId);
        return ResponseEntity.noContent().build();
    }

    // 상영일정 관리
    
    @GetMapping("/schedules")
    @Operation(summary = "모든 상영일정 조회", description = "시스템에 등록된 모든 상영일정 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공",
                content = @Content(schema = @Schema(implementation = ScheduleResponse.class))),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules() {
        List<ScheduleResponse> schedules = scheduleService.findAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/schedules")
    @Operation(summary = "상영일정 생성 (간단)", description = "간단한 정보로 새로운 상영일정을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = Schedule.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody CreateScheduleRequest request) {
        Schedule schedule = scheduleService.createSchedule(request.getTheaterId(), request.getMovieId(),
                request.getScheduleStartTime(), request.getScheduleEndTime());
        return ResponseEntity.ok(schedule);
    }

    @PostMapping("/schedules/advanced")
    @Operation(summary = "상영일정 생성 (고급)", description = "상세한 정보를 포함하여 새로운 상영일정을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "생성 성공",
                content = @Content(schema = @Schema(implementation = Schedule.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Schedule> createAdvancedSchedule(@Valid @RequestBody AdvancedScheduleRequest request) {
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
                    content = @Content(schema = @Schema(implementation = ScheduleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "상영일정을 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<ScheduleResponseDto> updateSchedule(
            @Parameter(description = "상영일정 ID", example = "1", required = true)
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleRequest request) {

        Schedule updatedSchedule = scheduleService.updateSchedule(
                scheduleId,
                request.getMovieId(),
                request.getTheaterId(),
                request.getScheduleStartTime(),
                request.getScheduleEndTime()
        );

        ScheduleResponseDto responseDto = ScheduleResponseDto.from(updatedSchedule);
        return ResponseEntity.ok(responseDto);
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

    @PutMapping("/use-coupon/{issueId}")
    @Operation(summary = "쿠폰 사용 처리", description = "관리자가 특정 발급된 쿠폰을 사용 처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "쿠폰 사용 처리 성공"),
        @ApiResponse(responseCode = "400", description = "이미 사용된 쿠폰"),
        @ApiResponse(responseCode = "404", description = "발급된 쿠폰을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<?> useCoupon(
            @Parameter(description = "발급 ID", example = "1", required = true) 
            @PathVariable Long issueId) {
        issueCouponService.useCoupon(issueId);
        
        log.info("Admin marked coupon issue {} as used", issueId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cancel-coupon-usage/{issueId}")
    @Operation(summary = "쿠폰 사용 취소", description = "관리자가 사용된 쿠폰의 사용 상태를 취소합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "쿠폰 사용 취소 성공"),
        @ApiResponse(responseCode = "400", description = "사용되지 않은 쿠폰"),
        @ApiResponse(responseCode = "404", description = "발급된 쿠폰을 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<?> cancelCouponUsage(
            @Parameter(description = "발급 ID", example = "1", required = true) 
            @PathVariable Long issueId) {
        issueCouponService.cancelCouponUsage(issueId);
        
        log.info("Admin canceled coupon usage for issue {}", issueId);
        return ResponseEntity.ok().build();
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

    @DeleteMapping("/users/{customerInputId}")
    @Operation(summary = "사용자 삭제", description = "특정 사용자를 시스템에서 삭제합니다. CASCADE 방식으로 사용자의 모든 관련 데이터(리뷰, 티켓, 결제, 카드, 쿠폰 등)도 함께 삭제됩니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (관리자 삭제 시도 등)"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "사용자 ID", example = "testuser123", required = true) 
            @PathVariable String customerInputId) {
        customerService.deleteCustomer(customerInputId);
        log.info("Admin deleted user: {} with all related data (CASCADE delete)", customerInputId);
        return ResponseEntity.noContent().build();
    }

    // ========== TemporalAdmin 통합 - 단순 생성 API ==========

    @PostMapping("/create-seat")
    @Operation(summary = "좌석 생성 (TemporalAdmin 호환)", description = "지정된 상영관에 좌석을 생성합니다.")
    public ResponseEntity<Seat> createSeat(@Valid @RequestBody CreateSeatRequest request) {
        Theater theater = new Theater();
        theater.setTheaterId(request.getTheaterId());

        Seat seat = new Seat();
        seat.setRowNumber(request.getSeatRow());
        seat.setColumnNumber(request.getSeatCol());
        seat.setTheater(theater);

        Seat createdSeat = seatService.createSeat(seat);
        return ResponseEntity.ok(createdSeat);
    }

    @PostMapping("/movie-genre")
    @Operation(summary ="영화장르 생성기", description="영화id와 genreid를 받아 생성")
    public ResponseEntity<?> createMovieGenre(@RequestBody CreateMovieGenreRequest request) {
        for (Long genreId : request.getMovieGenres()) {
            movieGenreService.createMovieGenre(request.getMovieId(), genreId);
        }
        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMovieGenreRequest {
        private Long movieId;
        private List<Long> movieGenres;
    }

    @PostMapping("/movie-actor")
    @Operation(summary = "영화배우 생성기", description = "영화 ID와 배우 ID 리스트를 받아 영화에 배우들을 추가합니다.")
    public ResponseEntity<?> createMovieActor(@RequestBody CreateMovieActorRequest request) {
        for (Long actorId : request.getMovieActors()) {
            movieActorService.createMovieActor(request.getMovieId(), actorId);
        }
        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMovieActorRequest {
        private Long movieId;
        private List<Long> movieActors;
    }

    // ========== TemporalAdmin 통합 DTO들 ==========

    /**
     * createTheater 전용DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTheaterRequest {
        @NotNull(message = "상영관 이름은 필수입니다")
        private String theaterName;
        
        @NotNull(message = "행 수는 필수입니다")
        private Integer rows;
        
        @NotNull(message = "열 수는 필수입니다")
        private Integer columns;
        
        @NotNull(message = "총 좌석수는 필수입니다")
        private Integer totalSeats;
    }

    /**
     * createSeat 전용 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateSeatRequest {
        @NotNull(message = "좌석 행 번호는 필수입니다.")
        private Integer seatRow;

        @NotNull(message = "좌석 열 번호는 필수입니다.")
        private Integer seatCol;

        @NotNull(message = "상영관 ID는 필수입니다.")
        private Long theaterId;
    }

    /**
     * createSchedule 전용 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateScheduleRequest {
        @NotNull(message = "상영관 ID는 필수입니다.")
        private Long theaterId;

        @NotNull(message = "영화 ID는 필수입니다.")
        private Long movieId;

        @NotNull(message = "시작 시간은 필수입니다.")
        @Future(message = "시작 시간은 현재보다 이후여야 합니다.")
        private LocalDateTime scheduleStartTime;

        @Future(message = "마감 시간은 현재보다 이후여야 합니다.")
        private LocalDateTime scheduleEndTime;


    }

    /**
     * 고급 상영일정 생성 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdvancedScheduleRequest {
        @NotNull(message = "영화 ID는 필수입니다.")
        private Long movieId;

        @NotNull(message = "상영관 ID는 필수입니다.")
        private Long theaterId;

        @NotNull(message = "상영 날짜는 필수입니다.")
        @Future(message = "상영 날짜는 현재보다 이후여야 합니다.")
        private LocalDate scheduleDate;

        @NotNull(message = "상영 회차는 필수입니다.")
        private Integer scheduleSequence;

        @NotNull(message = "시작 시간은 필수입니다.")
        @Future(message = "시작 시간은 현재보다 이후여야 합니다.")
        private LocalDateTime scheduleStartTime;
    }

}
