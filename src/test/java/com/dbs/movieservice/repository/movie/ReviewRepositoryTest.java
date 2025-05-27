package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.movie.Review;
import com.dbs.movieservice.repository.member.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReviewRepositoryTest {
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired MovieRepository movieRepository;
    @Autowired
    CustomerRepository customerRepository;

    @Test
    void 리뷰등록() {
        Movie movie = movieRepository.findById(1L).get();
        Customer customer = customerRepository.findById(1L).get();

        Review review = new Review();
        review.setMovie(movie);
        review.setCustomer(customer);
        review.setStarRating(4.5);
        review.setContentDesc("정말 재미있었어요!");
        review.setDateCreated(LocalDate.now());

        reviewRepository.save(review);
    }

}