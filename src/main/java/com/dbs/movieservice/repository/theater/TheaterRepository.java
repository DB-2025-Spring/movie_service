package com.dbs.movieservice.repository.theater;

import com.dbs.movieservice.domain.theater.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
}
