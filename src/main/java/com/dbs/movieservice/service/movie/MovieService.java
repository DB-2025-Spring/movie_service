package com.dbs.movieservice.service.movie;

import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.repository.movie.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public List<Movie> getNowShowingMovies() {
        return movieRepository.findNowShowingMovies();
    }

    public List<Movie> getUpcomingMovies() {
        return movieRepository.findUpcomingMovies();
    }

    public Movie getMovieDetails(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 영화가 없습니다. ID: " + id));
    }

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Long id, Movie updated) {
        updated.setMovieId(id);
        return movieRepository.save(updated);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}