package com.dbs.movieservice.service.movie;

import com.dbs.movieservice.domain.movie.Genre;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.movie.MovieGenre;
import com.dbs.movieservice.repository.movie.MovieActorRepository;
import com.dbs.movieservice.repository.movie.MovieGenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 해당 클레스는 클라이언트 입장에 최적화 된 메소드들이 아님. 추후 편의성 개선 바람.
 */

@Service
@Slf4j

public class MovieGenreService {
    private final MovieGenreRepository movieGenreRepository;
    private final MovieService movieService;
    private final GenreService genreService;
    private final MovieActorService movieActorService;
    private final MovieActorRepository movieActorRepository;

    MovieGenreService(MovieGenreRepository movieGenreRepository, MovieService movieService, GenreService genreService, MovieActorService movieActorService, MovieActorRepository movieActorRepository) {
        this.movieGenreRepository = movieGenreRepository;
        this.movieService = movieService;
        this.genreService = genreService;
        this.movieActorService = movieActorService;
        this.movieActorRepository = movieActorRepository;
    }

    public MovieGenre createMovieGenre(Long movieId, Long genreId) {
        Movie movie = movieService.findMovieById(movieId).orElseThrow(RuntimeException::new);
        Genre genre = genreService.findGenreById(genreId).orElseThrow(RuntimeException::new);
        MovieGenre movieGenre = new MovieGenre(movie, genre);
        return movieGenreRepository.save(movieGenre);
    }

    @Transactional(readOnly = true)
    public void deleteAllGenresByMovieId(Long movieId) {
        movieActorRepository.deleteByMovie_MovieId(movieId);
    }

}
