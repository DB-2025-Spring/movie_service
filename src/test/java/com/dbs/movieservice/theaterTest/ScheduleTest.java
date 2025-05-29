package com.dbs.movieservice.theaterTest;


import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.repository.movie.MovieRepository;
import com.dbs.movieservice.repository.theater.ScheduleRepository;
import com.dbs.movieservice.repository.theater.SeatRepository;
import com.dbs.movieservice.repository.theater.TheaterRepository;
import com.dbs.movieservice.service.theater.ScheduleService;
import com.dbs.movieservice.service.theater.SeatService;
import com.dbs.movieservice.service.theater.TheaterService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.time.LocalDateTime;

@SpringBootTest
public class ScheduleTest {
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private TheaterService theaterService;
    @Autowired
    private SeatService seatService;
    @Autowired
    private SeatRepository seatRepository;

    @Test
    void createTheaterAndSeat() {
        // 1. 상영관 생성
        Theater theater = new Theater();
        theater.setTheaterName("테스트관");
        theater.setTotalSeats(0);
        Theater savedTheater = theaterService.createTheater(theater);

        // 2. 좌석 생성
        Seat seat1 = new Seat();
        seat1.setTheater(savedTheater);
        seat1.setRowNumber(1);
        seat1.setColumnNumber(1);
        seatService.createSeat(seat1);
        Seat seat2 = new Seat();
        seat2.setTheater(savedTheater);
        seat2.setRowNumber(1);
        seat2.setColumnNumber(2);
        seatService.createSeat(seat2);
    }

}
