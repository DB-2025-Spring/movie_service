package com.dbs.movieservice.service.theater;

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



    public Map<Long, Long> countAvailableSeatMap(List<Long> scheduleIds) {
        List<Object[]> results = seatAvailableRepository.countAvailableSeatsByScheduleIds(scheduleIds);

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    public List<SeatAvailable> getAvailableSeatForSchedule(Schedule schedule) {
        return seatAvailableRepository.findByScheduleId(schedule.getScheduleId());
    }

    public Boolean isAvailableForTicket(Schedule schedule, List<Seat> seats) {
        Long scheduleId = schedule.getScheduleId();
        int seatCount = seats.size();
        List<Long>seatsId = seats.stream().map(Seat::getSeatId).toList();
//        return seatCount == seatAvailableRepository.countAvailableSeat(scheduleId, seatsId);
        try{
            return seatCount == seatAvailableRepository.countAvailableSeat(scheduleId, seatsId);
        }catch(PessimisticLockException | LockTimeoutException e) {
            return false;
        }
    }

    public Boolean updateAvailableSeat(Schedule schedule, List<Seat> seat, String updateType) {
        Long scheduleId = schedule.getScheduleId();
        List<Long>seatsId = seat.stream().map(Seat::getSeatId).toList();

        return seat.size()==seatAvailableRepository.updateIsBook(updateType, scheduleId, seatsId);
    }


}
