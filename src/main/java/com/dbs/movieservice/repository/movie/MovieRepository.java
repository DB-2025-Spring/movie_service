package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
