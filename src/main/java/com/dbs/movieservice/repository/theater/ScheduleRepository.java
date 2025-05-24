package com.dbs.movieservice.repository.theater;

import com.dbs.movieservice.domain.theater.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByScheduleDateAndTheater_TheaterId(LocalDate scheduleDate, Long theaterId);

    @Query("SELECT s FROM Schedule s " +
            "WHERE s.movie.movieId = :movieId " +
            "AND s.scheduleStartTime BETWEEN :startOfToday AND :endOfTargetDay")
    List<Schedule> findSchedulesForNext7Days(@Param("movieId") Long movieId,
                                                @Param("startOfToday") LocalDateTime startOfToday,
                                                @Param("endOfTargetDay") LocalDateTime endOfTargetDay);

    @Query("SELECT s FROM Schedule s WHERE s.scheduleDate = :scheduleDate")
    List<Schedule> findSchedulesByDate(@Param("scheduleDate") LocalDate scheduleDate);
}
