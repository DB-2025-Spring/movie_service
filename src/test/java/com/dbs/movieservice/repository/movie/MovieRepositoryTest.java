package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class MovieRepositoryTest {

    @Autowired
    MovieRepository movieRepository;

    @Test
    void 영화등록_테스트() {
        Movie movie = new Movie();
        movie.setMovieName("Interstellar");
        movie.setDirectorName("Christopher Nolan");
        movie.setCoo("USA");
        movie.setReleaseDate(LocalDate.of(2014, 11, 7));

        movieRepository.save(movie);
    }
}