package com.dbs.movieservice.repository.theater;

import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.theater.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByScheduleDateAndTheater_TheaterId(LocalDate scheduleDate, Long theaterId);
    List<Schedule> findByScheduleDateAndMovie_MovieId(LocalDate scheduleDate, Long movieId);
    @Query("SELECT s FROM Schedule s " +
            "LEFT JOIN FETCH s.theater " +
            "WHERE s.movie.movieId = :movieId " +
            "AND s.scheduleStartTime BETWEEN :startOfToday AND :endOfTargetDay")
    List<Schedule> findSchedulesForNext7Days(
            @Param("movieId") Long movieId,
            @Param("startOfToday") LocalDateTime startOfToday,
            @Param("endOfTargetDay") LocalDateTime endOfTargetDay);


    /**
     * ====V2용 query====
     */
    @Query(value = """
    SELECT DISTINCT TRUNC(s.schedule_start_time)
    FROM schedule s
    WHERE s.schedule_start_time BETWEEN :start AND :end
    ORDER BY TRUNC(s.schedule_start_time)
    """, nativeQuery = true)
    List<Object> findScheduleDateBetweenTwoDateNative(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    @Query("SELECT DISTINCT s FROM Schedule s Left Join fetch s.movie m WHERE s.scheduleDate = :scheduleDate")
    List<Schedule> findSchedulesByDate(@Param("scheduleDate") LocalDate scheduleDate);
}
