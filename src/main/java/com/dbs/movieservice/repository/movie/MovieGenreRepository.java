package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, MovieGenre.MovieGenreId> {
    // 특정 영화에 속한 장르들
    List<MovieGenre> findByMovie_MovieId(Long movieId);

    // 특정 장르에 속한 영화들
    List<MovieGenre> findByGenre_GenreId(Long genreId);
}
