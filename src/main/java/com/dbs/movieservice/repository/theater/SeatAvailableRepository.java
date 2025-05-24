package com.dbs.movieservice.repository.theater;

import com.dbs.movieservice.domain.theater.SeatAvailable;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "1000")  // 밀리초 단위
    })
    @Query("Select Count(sa) From SeatAvailable sa Where sa.id.scheduleId=:scheduleId and sa.id.seatId In :seatsId and sa.isBooked='f'")
    long countAvailableSeat(@Param("scheduleId") Long scheduleId, @Param("seatsId") List<Long> seatsId);
}
