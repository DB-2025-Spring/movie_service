package com.dbs.movieservice.repository.theater;

import com.dbs.movieservice.domain.theater.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
