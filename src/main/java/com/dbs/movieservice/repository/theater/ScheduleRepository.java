package com.dbs.movieservice.repository.theater;

import com.dbs.movieservice.domain.theater.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByScheduleDate(LocalDate scheduleDate);
}
