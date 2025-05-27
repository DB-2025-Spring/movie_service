package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.movie.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 영화별 리뷰 조회
    List<Review> findByMovie(Movie movie);

    // 유저별 리뷰 조회
    List<Review> findByCustomer(Customer customer);

    // 영화 ID로 직접 조회
    List<Review> findByMovie_MovieId(Long movieId);
}
