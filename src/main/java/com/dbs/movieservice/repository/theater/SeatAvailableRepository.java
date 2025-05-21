package com.dbs.movieservice.repository.theater;

import com.dbs.movieservice.domain.theater.SeatAvailable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatAvailableRepository extends JpaRepository<SeatAvailable, SeatAvailable.SeatAvailableId> {
}
