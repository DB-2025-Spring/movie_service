package com.dbs.movieservice.service.theater;

import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.SeatAvailable;
import com.dbs.movieservice.repository.theater.ScheduleRepository;
import com.dbs.movieservice.repository.theater.SeatAvailableRepository;
import com.dbs.movieservice.repository.theater.SeatRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeatAvailableService {
    private final SeatRepository seatRepository;
    private final SeatAvailableRepository seatAvailableRepository;

    public SeatAvailableService(SeatRepository seatRepository,ScheduleRepository scheduleRepository,SeatAvailableRepository seatAvailableRepository) {
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
            seatAvailable.setIsBooked("f");

            return seatAvailable;
        }).toList();

        seatAvailableRepository.saveAll(seatAvailableList);
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
}
