package com.dbs.movieservice.repository.theater;

import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByTheater_TheaterId(Long theaterId);
    
    int countByTheater_TheaterId(Long theaterId);

    Seat findByTheaterAndRowNumberAndColumnNumber(Theater theater, Integer rowNumber, Integer columnNumber);
}
