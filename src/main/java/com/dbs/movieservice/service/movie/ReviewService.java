package com.dbs.movieservice.service.movie;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.movie.Review;
import com.dbs.movieservice.dto.ReviewCreateRequest;
import com.dbs.movieservice.dto.ReviewDto;
import com.dbs.movieservice.dto.ReviewUpdateRequest;
import com.dbs.movieservice.repository.member.CustomerRepository;
import com.dbs.movieservice.repository.movie.MovieRepository;
import com.dbs.movieservice.repository.movie.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final MovieRepository movieRepository;

    /**
     * 특정 영화의 리뷰 조회
     */
    public List<ReviewDto> getReviewsByMovie(Long movieId) {
        return reviewRepository.findByMovie_MovieId(movieId).stream()
                .map(ReviewDto::new)
                .toList();
    }

    /**
     * 내 리뷰 목록 조회
     */
    public List<ReviewDto> getReviewsByCustomer(Long customerId) {
        return reviewRepository.findByCustomer_CustomerId(customerId).stream()
                .map(ReviewDto::new)
                .toList();
    }

    /**
     * 리뷰 등록
     */
    @Transactional
    public ReviewDto createReview(ReviewCreateRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new EntityNotFoundException("영화 없음"));
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("고객 없음"));

        Review review = new Review();
        review.setMovie(movie);
        review.setCustomer(customer);
        review.setStarRating(request.getStarRating());
        review.setContentDesc(request.getContentDesc());
        review.setDateCreated(LocalDate.now());

        return new ReviewDto(reviewRepository.save(review));
    }

    /**
     * 리뷰 수정
     */
    @Transactional
    public ReviewDto updateReview(Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰 없음"));
        review.setStarRating(request.getStarRating());
        review.setContentDesc(request.getContentDesc());
        return new ReviewDto(review);
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new EntityNotFoundException("리뷰 없음");
        }
        reviewRepository.deleteById(reviewId);
    }

    /**
     * 특정 고객이 특정 영화에 대해 작성한 리뷰 조회 (무비로그용)
     */
    public Review findReviewByCustomerAndMovie(Long customerId, Long movieId) {
        return reviewRepository.findByCustomer_CustomerIdAndMovie_MovieId(customerId, movieId)
                .orElse(null);
    }

    /**
     * 리뷰 소유자 확인
     */
    public boolean isReviewOwner(Long reviewId, Long customerId) {
        return reviewRepository.findById(reviewId)
                .map(review -> review.getCustomer().getCustomerId().equals(customerId))
                .orElse(false);
    }
}
