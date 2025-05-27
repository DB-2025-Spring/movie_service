package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    // 장르 이름으로 검색
    Optional<Genre> findByGenreName(String genreName);
}
