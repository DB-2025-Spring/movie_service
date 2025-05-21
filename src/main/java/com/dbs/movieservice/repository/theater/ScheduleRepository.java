package com.dbs.movieservice.repository.theater;

import com.dbs.movieservice.domain.theater.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
