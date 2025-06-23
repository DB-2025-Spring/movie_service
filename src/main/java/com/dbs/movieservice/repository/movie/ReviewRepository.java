package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.movie.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 특정 영화의 리뷰들
    List<Review> findByMovie_MovieId(Long movieId);

    // 특정 고객의 리뷰들 (마이페이지용)
    List<Review> findByCustomer_CustomerId(Long customerId);

    // 영화 + 고객 조합 리뷰 (리뷰 중복 방지용)
    Optional<Review> findByMovie_MovieIdAndCustomer_CustomerId(Long movieId, Long customerId);
    
    // 특정 고객이 특정 영화에 대해 작성한 리뷰 조회 (무비로그용)
    Optional<Review> findByCustomer_CustomerIdAndMovie_MovieId(Long customerId, Long movieId);
}
