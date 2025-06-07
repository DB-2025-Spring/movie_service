package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    // 자동완성용
    @Query("SELECT m.movieName FROM Movie m WHERE LOWER(m.movieName) LIKE LOWER(CONCAT(:keyword, '%'))")
    List<String> findAutoCompleteTitles(@Param("keyword") String keyword);

    // 키워드 검색
    @Query("SELECT DISTINCT m FROM Movie m " +
            "LEFT JOIN m.movieActors ma " +
            "LEFT JOIN ma.actor a " +
            "WHERE LOWER(m.movieName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(m.directorName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(a.actorName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Movie> searchMoviesByKeyword(@Param("keyword") String keyword);

    // 개봉일 기준 정렬
    List<Movie> findAllByOrderByReleaseDateDesc();

    // 현재 상영작 (release_date <= 오늘, end_date >= 오늘)
    @Query("SELECT m FROM Movie m WHERE m.releaseDate <= CURRENT_DATE AND m.endDate >= CURRENT_DATE")
    List<Movie> findNowShowingMovies();

    // 상영 예정작 (release_date > 오늘)
    @Query("SELECT m FROM Movie m WHERE m.releaseDate > CURRENT_DATE")
    List<Movie> findUpcomingMovies();

}
