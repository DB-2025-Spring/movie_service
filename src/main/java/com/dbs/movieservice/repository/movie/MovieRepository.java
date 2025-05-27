package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    // 영화 이름으로 검색
    List<Movie> findByMovieNameContaining(String keyword);

    // 개봉일 기준 정렬
    List<Movie> findAllByOrderByReleaseDateDesc();

    // 감독 이름으로 검색
    List<Movie> findByDirectorName(String directorName);

    // 국가로 검색
    List<Movie> findByCoo(String coo);

    // 현재 상영작 (release_date <= 오늘, end_date >= 오늘)
    @Query("SELECT m FROM Movie m WHERE m.releaseDate <= CURRENT_DATE AND m.endDate >= CURRENT_DATE")
    List<Movie> findNowShowingMovies();

    // 상영 예정작 (release_date > 오늘)
    @Query("SELECT m FROM Movie m WHERE m.releaseDate > CURRENT_DATE")
    List<Movie> findUpcomingMovies();

}
