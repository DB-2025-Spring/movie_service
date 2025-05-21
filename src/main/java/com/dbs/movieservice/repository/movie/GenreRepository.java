package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
