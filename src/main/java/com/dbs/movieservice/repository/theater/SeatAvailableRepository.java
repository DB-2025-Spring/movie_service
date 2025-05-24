package com.dbs.movieservice.repository.theater;

import com.dbs.movieservice.domain.theater.SeatAvailable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatAvailableRepository extends JpaRepository<SeatAvailable, SeatAvailable.SeatAvailableId> {


    @Query("SELECT sa.id.scheduleId, COUNT(sa) " +
            "FROM SeatAvailable sa " +
            "WHERE sa.id.scheduleId IN :scheduleIds AND sa.isBooked = 'f' " +
            "GROUP BY sa.id.scheduleId")
    List<Object[]> countAvailableSeatsByScheduleIds(@Param("scheduleIds") List<Long> scheduleIds);

    @Query("SELECT sa FROM SeatAvailable sa WHERE sa.id.scheduleId = :scheduleId AND sa.isBooked='f'")
    List<SeatAvailable> findByScheduleId(@Param("scheduleId") Long scheduleId);
}
