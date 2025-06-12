package com.dbs.movieservice.repository.theater;

import com.dbs.movieservice.domain.theater.SeatAvailable;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatAvailableRepository extends JpaRepository<SeatAvailable, SeatAvailable.SeatAvailableId> {


    @Query("SELECT sa.id.scheduleId, COUNT(sa) " +
            "FROM SeatAvailable sa " +
            "WHERE sa.id.scheduleId IN :scheduleIds AND sa.isBooked = 'F' " +
            "GROUP BY sa.id.scheduleId")
    List<Object[]> countAvailableSeatsByScheduleIds(@Param("scheduleIds") List<Long> scheduleIds);

    @Query("SELECT sa FROM SeatAvailable sa WHERE sa.id.scheduleId = :scheduleId")
    List<SeatAvailable> findByScheduleId(@Param("scheduleId") Long scheduleId);

    /**
     * 기존에 사용했던 코드. 비관적 locking이 걸리지 않기떄문에 사용하지 않음.
     * @param scheduleId
     * @param seatsId
     * @return
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "1000")  // 밀리초 단위
    })
    @Query("Select Count(sa) From SeatAvailable sa Where sa.id.scheduleId=:scheduleId and sa.id.seatId In :seatsId and sa.isBooked='F'")
    long countAvailableSeat(@Param("scheduleId") Long scheduleId, @Param("seatsId") List<Long> seatsId);

    @Modifying(clearAutomatically = true)
    @Query("Update SeatAvailable sa Set sa.isBooked =:updateType Where sa.id.scheduleId=:scheduleId and sa.id.seatId In :seatsId")
    int updateIsBook(@Param("updateType")String updateType, @Param("scheduleId")Long scheduleId, @Param("seatsId")List<Long> seatsId);

    /**
     * countAvailableSea함수 대체. count와 같은 집계함수는 pessimitic_read가 불가능
     * @param scheduleId
     * @param seatsId
     * @return
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "1000")
    })
    @Query("SELECT sa FROM SeatAvailable sa WHERE sa.id.scheduleId = :scheduleId AND sa.id.seatId IN :seatsId AND sa.isBooked = 'F'")
    List<SeatAvailable> findAvailableSeatsForUpdate(
            @Param("scheduleId") Long scheduleId,
            @Param("seatsId") List<Long> seatsId
    );
}
