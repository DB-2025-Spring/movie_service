package com.dbs.movieservice.controller;

import com.dbs.movieservice.controller.dto.*;
import com.dbs.movieservice.domain.member.ClientLevel;
import com.dbs.movieservice.domain.movie.Actor;
import com.dbs.movieservice.domain.movie.Genre;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.domain.ticketing.Coupon;
import com.dbs.movieservice.service.member.ClientLevelService;
import com.dbs.movieservice.service.movie.ActorService;
import com.dbs.movieservice.service.movie.GenreService;
import com.dbs.movieservice.service.movie.MovieService;
import com.dbs.movieservice.service.theater.ScheduleService;
import com.dbs.movieservice.service.theater.SeatService;
import com.dbs.movieservice.service.theater.TheaterService;
import com.dbs.movieservice.service.ticketing.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final MovieService movieService;
    private final ActorService actorService;
    private final GenreService genreService;
    private final TheaterService theaterService;
    private final SeatService seatService;
    private final ScheduleService scheduleService;
    private final CouponService couponService;
    private final ClientLevelService clientLevelService;

    // ========== 영화 관리 ==========
    
    @GetMapping("/movies")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.findAllMovies();
        return ResponseEntity.ok(movies);
    }

    @PostMapping("/movies")
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
    public ResponseEntity<Movie> updateMovie(@PathVariable Long movieId, 
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
    public ResponseEntity<Void> deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.noContent().build();
    }

    // ========== 배우 관리 ==========
    
    @GetMapping("/actors")
    public ResponseEntity<List<Actor>> getAllActors() {
        List<Actor> actors = actorService.findAllActors();
        return ResponseEntity.ok(actors);
    }

    @PostMapping("/actors")
    public ResponseEntity<Actor> createActor(@Valid @RequestBody ActorRequest request) {
        Actor actor = new Actor();
        actor.setActorName(request.getActorName());
        
        Actor savedActor = actorService.saveActor(actor);
        return ResponseEntity.ok(savedActor);
    }

    @PutMapping("/actors/{actorId}")
    public ResponseEntity<Actor> updateActor(@PathVariable Long actorId,
                                           @Valid @RequestBody ActorRequest request) {
        Actor updatedActor = actorService.updateActor(actorId, request.getActorName());
        return ResponseEntity.ok(updatedActor);
    }

    @DeleteMapping("/actors/{actorId}")
    public ResponseEntity<Void> deleteActor(@PathVariable Long actorId) {
        actorService.deleteActor(actorId);
        return ResponseEntity.noContent().build();
    }

    // ========== 장르 관리 ==========
    
    @GetMapping("/genres")
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = genreService.findAllGenres();
        return ResponseEntity.ok(genres);
    }

    @PostMapping("/genres")
    public ResponseEntity<Genre> createGenre(@Valid @RequestBody GenreRequest request) {
        Genre genre = new Genre();
        genre.setGenreName(request.getGenreName());
        
        Genre savedGenre = genreService.saveGenre(genre);
        return ResponseEntity.ok(savedGenre);
    }

    @PutMapping("/genres/{genreId}")
    public ResponseEntity<Genre> updateGenre(@PathVariable Long genreId,
                                           @Valid @RequestBody GenreRequest request) {
        Genre updatedGenre = genreService.updateGenre(genreId, request.getGenreName());
        return ResponseEntity.ok(updatedGenre);
    }

    @DeleteMapping("/genres/{genreId}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long genreId) {
        genreService.deleteGenre(genreId);
        return ResponseEntity.noContent().build();
    }

    // ========== 상영관 관리 ==========
    
    @GetMapping("/theaters")
    public ResponseEntity<List<Theater>> getAllTheaters() {
        List<Theater> theaters = theaterService.findAllTheaters();
        return ResponseEntity.ok(theaters);
    }

    @PostMapping("/theaters")
    public ResponseEntity<Theater> createTheater(@Valid @RequestBody TheaterRequest request) {
        Theater theater = new Theater();
        theater.setTheaterName(request.getTheaterName());
        theater.setTotalSeats(request.getTotalSeats());
        
        Theater savedTheater = theaterService.saveTheater(theater);
        return ResponseEntity.ok(savedTheater);
    }

    @PutMapping("/theaters/{theaterId}")
    public ResponseEntity<Theater> updateTheater(@PathVariable Long theaterId,
                                               @Valid @RequestBody TheaterRequest request) {
        Theater updatedTheater = theaterService.updateTheater(
            theaterId, request.getTheaterName(), request.getTotalSeats()
        );
        return ResponseEntity.ok(updatedTheater);
    }

    @DeleteMapping("/theaters/{theaterId}")
    public ResponseEntity<Void> deleteTheater(@PathVariable Long theaterId) {
        theaterService.deleteTheater(theaterId);
        return ResponseEntity.noContent().build();
    }

    // ========== 좌석 관리 ==========
    
    @GetMapping("/seats")
    public ResponseEntity<List<Seat>> getAllSeats() {
        List<Seat> seats = seatService.findAllSeats();
        return ResponseEntity.ok(seats);
    }

    @PostMapping("/seats")
    public ResponseEntity<Seat> createSeat(@Valid @RequestBody SeatRequest request) {
        Seat savedSeat = seatService.saveSeat(
            request.getTheaterId(), request.getRowNumber(), request.getColumnNumber()
        );
        return ResponseEntity.ok(savedSeat);
    }

    @PutMapping("/seats/{seatId}")
    public ResponseEntity<Seat> updateSeat(@PathVariable Long seatId,
                                         @Valid @RequestBody SeatRequest request) {
        Seat updatedSeat = seatService.updateSeat(
            seatId, request.getTheaterId(), request.getRowNumber(), request.getColumnNumber()
        );
        return ResponseEntity.ok(updatedSeat);
    }

    @DeleteMapping("/seats/{seatId}")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long seatId) {
        seatService.deleteSeat(seatId);
        return ResponseEntity.noContent().build();
    }

    // ========== 상영일정 관리 ==========
    
    @GetMapping("/schedules")
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.findAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/schedules")
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody ScheduleRequest request) {
        Schedule savedSchedule = scheduleService.saveSchedule(
            request.getMovieId(), request.getTheaterId(), request.getScheduleDate(),
            request.getScheduleSequence(), request.getScheduleStartTime()
        );
        return ResponseEntity.ok(savedSchedule);
    }

    @PutMapping("/schedules/{scheduleId}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long scheduleId,
                                                 @Valid @RequestBody ScheduleRequest request) {
        Schedule updatedSchedule = scheduleService.updateSchedule(
            scheduleId, request.getMovieId(), request.getTheaterId(),
            request.getScheduleDate(), request.getScheduleSequence(), request.getScheduleStartTime()
        );
        return ResponseEntity.ok(updatedSchedule);
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    // ========== 쿠폰 관리 ==========
    
    @GetMapping("/coupons")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.findAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @PostMapping("/coupons")
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody CouponRequest request) {
        Coupon savedCoupon = couponService.saveCoupon(
            request.getCouponName(), request.getCouponDescription(),
            request.getStartDate(), request.getEndDate()
        );
        return ResponseEntity.ok(savedCoupon);
    }

    @PutMapping("/coupons/{couponId}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long couponId,
                                             @Valid @RequestBody CouponRequest request) {
        Coupon updatedCoupon = couponService.updateCoupon(
            couponId, request.getCouponName(), request.getCouponDescription(),
            request.getStartDate(), request.getEndDate()
        );
        return ResponseEntity.ok(updatedCoupon);
    }

    @DeleteMapping("/coupons/{couponId}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }

    // ========== 고객등급 관리 ==========
    
    @GetMapping("/client-levels")
    public ResponseEntity<List<ClientLevel>> getAllClientLevels() {
        List<ClientLevel> levels = clientLevelService.findAllLevels();
        return ResponseEntity.ok(levels);
    }

    @PostMapping("/client-levels")
    public ResponseEntity<ClientLevel> createClientLevel(@Valid @RequestBody ClientLevelRequest request) {
        ClientLevel clientLevel = new ClientLevel();
        clientLevel.setLevelName(request.getLevelName());
        clientLevel.setRewardRate(request.getRewardRate());
        
        ClientLevel savedLevel = clientLevelService.saveLevel(clientLevel);
        return ResponseEntity.ok(savedLevel);
    }

    @PutMapping("/client-levels/{levelId}")
    public ResponseEntity<ClientLevel> updateClientLevel(@PathVariable Integer levelId,
                                                       @Valid @RequestBody ClientLevelRequest request) {
        ClientLevel updatedLevel = clientLevelService.updateLevel(
            levelId, request.getLevelName(), request.getRewardRate()
        );
        return ResponseEntity.ok(updatedLevel);
    }

    @DeleteMapping("/client-levels/{levelId}")
    public ResponseEntity<Void> deleteClientLevel(@PathVariable Integer levelId) {
        clientLevelService.deleteLevel(levelId);
        return ResponseEntity.noContent().build();
    }
}