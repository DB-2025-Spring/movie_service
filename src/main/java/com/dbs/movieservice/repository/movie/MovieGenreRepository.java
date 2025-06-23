package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.movie.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, MovieGenre.MovieGenreId> {
    // 특정 영화에 속한 장르들
    List<MovieGenre> findByMovie_MovieId(Long movieId);

    // 특정 장르에 속한 영화들
    List<MovieGenre> findByGenre_GenreId(Long genreId);

    // 장르 ID 기준으로 영화 직접 가져오기 (DTO화도 가능)
    @Query("SELECT mg.movie FROM MovieGenre mg WHERE mg.genre.genreId = :genreId")
    List<Movie> findMoviesByGenreId(@Param("genreId") Long genreId);

    void deleteByMovie_MovieId(Long movieId);
}
