package com.dbs.movieservice.controller.movie;

import com.dbs.movieservice.domain.movie.Review;
import com.dbs.movieservice.repository.member.CustomerRepository;
import com.dbs.movieservice.repository.movie.MovieRepository;
import com.dbs.movieservice.repository.movie.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final CustomerRepository customerRepository;

    @PostMapping
    public Review writeReview(@RequestBody Review review) {
        return reviewRepository.save(review);
    }

    @GetMapping("/movie/{movieId}")
    public List<Review> getReviewsByMovie(@PathVariable Long movieId) {
        return reviewRepository.findByMovie_MovieId(movieId);
    }

    @PutMapping("/{reviewId}")
    public Review updateReview(@PathVariable Long reviewId, @RequestBody Review updatedReview) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        review.setContentDesc(updatedReview.getContentDesc());
        review.setStarRating(updatedReview.getStarRating());
        return reviewRepository.save(review);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
