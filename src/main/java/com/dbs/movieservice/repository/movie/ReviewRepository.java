package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
