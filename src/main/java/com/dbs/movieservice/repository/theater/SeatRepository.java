package com.dbs.movieservice.repository.theater;

import com.dbs.movieservice.domain.theater.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByTheater_TheaterId(Long theaterId);
}
