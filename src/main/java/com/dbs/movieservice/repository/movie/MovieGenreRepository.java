package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, MovieGenre.MovieGenreId> {
}
