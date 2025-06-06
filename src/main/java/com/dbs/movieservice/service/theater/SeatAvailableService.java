package com.dbs.movieservice.service.theater;

import com.dbs.movieservice.config.exception.BusinessException;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.SeatAvailable;
import com.dbs.movieservice.repository.theater.ScheduleRepository;
import com.dbs.movieservice.repository.theater.SeatAvailableRepository;
import com.dbs.movieservice.repository.theater.SeatRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PessimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeatAvailableService {
//    @PersistenceContext
//    private EntityManager entityManager;
    private final SeatRepository seatRepository;
    private final SeatAvailableRepository seatAvailableRepository;

    public SeatAvailableService(SeatRepository seatRepository,SeatAvailableRepository seatAvailableRepository) {
        this.seatRepository = seatRepository;
        this.seatAvailableRepository = seatAvailableRepository;
    }

    /**
     * 스케쥴을 전달받아, sa를 생성
     * @param schedule
     */
    public void createSeatAvailableForSchedule(Schedule schedule) {
        Long theaterId = schedule.getTheater().getTheaterId();

        List<Seat> seats = seatRepository.findByTheater_TheaterId(theaterId);
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("해당 상영관에는 등록된 좌석이 없습니다.");
        }


        List<SeatAvailable> seatAvailableList = seats.stream().map(seat -> {
            SeatAvailable.SeatAvailableId id = new SeatAvailable.SeatAvailableId(schedule.getScheduleId(), seat.getSeatId());

            SeatAvailable seatAvailable = new SeatAvailable();
            seatAvailable.setId(id);
            seatAvailable.setSchedule(schedule);
            seatAvailable.setSeat(seat);
            seatAvailable.setIsBooked("F");

            return seatAvailable;
        }).toList();

        seatAvailableRepository.saveAll(seatAvailableList);
        //    public void createSeatAvailableForSchedule(Schedule schedule) {
//        List<Seat> seats = seatRepository.findByTheater_TheaterId(schedule.getTheater().getTheaterId());
//
//        int batchSize = 50;
//        for (int i = 0; i < seats.size(); i++) {
//            Seat seat = seats.get(i);
//            SeatAvailable seatAvailable = new SeatAvailable();
//            seatAvailable.setId(new SeatAvailable.SeatAvailableId(schedule.getScheduleId(), seat.getSeatId()));
//            seatAvailable.setSchedule(schedule);
//            seatAvailable.setSeat(seat);
//            seatAvailable.setIsBooked("f");
//
//            entityManager.persist(seatAvailable);
//
//            if (i > 0 && i % batchSize == 0) {
//                entityManager.flush();
//                entityManager.clear();
//            }
//        }
//
//        entityManager.flush();
//        entityManager.clear();
//    }
    }


    /**
     * 영화 상영 일정(id)들을 전달받아, 해당 일정들에 남은 좌석수들을 조회.
     * @param scheduleIds
     * @return list<ScheduleId, 남은좌석수>
     */
    public Map<Long, Long> countAvailableSeatMap(List<Long> scheduleIds) {
        List<Object[]> results = seatAvailableRepository.countAvailableSeatsByScheduleIds(scheduleIds);

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    /**
     * 하나의 상영일정에 대해, 사용가능한 좌석들을 검색
     * @param schedule
     * @return
     */
    public List<SeatAvailable> getAvailableSeatForSchedule(Schedule schedule) {
        return seatAvailableRepository.findByScheduleId(schedule.getScheduleId());
    }

    /**
     *
     * @param schedule
     * @param seats
     * @return
     */
    public void isAvailableForTicket(Schedule schedule, List<Seat> seats) {
        Long scheduleId = schedule.getScheduleId();
        List<Long>seatsId = seats.stream().map(Seat::getSeatId).toList();

        try {
            List<SeatAvailable> availableSeats = seatAvailableRepository.findAvailableSeatsForUpdate(scheduleId, seatsId);
            if (availableSeats.size() != seats.size()) {
                throw new BusinessException(
                        "예매하려는 좌석 중 이미 예약된 좌석이 존재합니다.",
                        HttpStatus.CONFLICT,
                        "SEAT_ALREADY_BOOKED"
                );
            }
        } catch (PessimisticLockException | LockTimeoutException e) {
            throw new BusinessException(
                    "좌석 확인 중 다른 트랜잭션에 의해 잠금 충돌이 발생했습니다.",
                    HttpStatus.LOCKED,
                    "SEAT_LOCK_CONFLICT"
            );
        }
    }

    public Boolean updateAvailableSeat(Schedule schedule, List<Seat> seat, String updateType) {
        Long scheduleId = schedule.getScheduleId();
        List<Long>seatsId = seat.stream().map(Seat::getSeatId).toList();

        return seat.size()==seatAvailableRepository.updateIsBook(updateType, scheduleId, seatsId);
    }


}
