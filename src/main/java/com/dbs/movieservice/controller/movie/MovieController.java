package com.dbs.movieservice.controller.movie;

import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.repository.movie.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieRepository movieRepository;

    @GetMapping("/now-showing")
    public List<Movie> getNowShowingMovies() {
        // 상영 중인 영화만 조회
        return movieRepository.findNowShowingMovies();
    }

    @GetMapping("/upcoming")
    public List<Movie> getUpcomingMovies() {
        return movieRepository.findUpcomingMovies();
    }

    @GetMapping("/{id}")
    public Movie getMovieDetails(@PathVariable Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "영화 없음"));
    }
}
