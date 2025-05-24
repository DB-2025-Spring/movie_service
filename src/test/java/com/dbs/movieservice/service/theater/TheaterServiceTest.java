package com.dbs.movieservice.service.theater;

import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.repository.movie.MovieRepository;
import com.dbs.movieservice.repository.theater.ScheduleRepository;
import com.dbs.movieservice.repository.theater.SeatAvailableRepository;
import com.dbs.movieservice.repository.theater.SeatRepository;
import com.dbs.movieservice.repository.theater.TheaterRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TheaterServiceTest {
    @Autowired
    private TheaterService theaterService;

    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private SeatService seatService;
    @Autowired
    private SeatAvailableRepository seatAvailableRepository;
    @Autowired
    private SeatAvailableRepository seatAvailableService;
    @Test
    void createTheater_shouldPersistEntity() {
        // given
        Theater theater = new Theater();
        theater.setTheaterName("1번상영관");
        theater.setTotalSeats(150);

        // when
        Theater saved = theaterService.createTheater(theater);

        // then
        assertThat(saved.getTheaterId()).isNotNull(); // 자동 생성된 ID
        assertThat(saved.getTheaterName()).isEqualTo("1번상영관");
        assertThat(saved.getTotalSeats()).isEqualTo(150);
        assertThat(theaterRepository.findById(saved.getTheaterId())).isPresent(); // 실제 DB에 저장됨
    }

    @Test
    void createSchedule_shouldSucceed() {
        Long theaterId = 21L;
        Long movieId = 1L;
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0); // 내일 오후 2시

        Schedule schedule = scheduleService.createSchedule(theaterId, movieId, startTime);

        System.out.println("Created schedule ID: " + schedule.getScheduleId());
        System.out.println("Start time: " + schedule.getScheduleStartTime());
        System.out.println("Sequence: " + schedule.getScheduleSequence());
    }

    @Test
    void createSchedule_shouldSucceed_whenNotOverlapping() {
        Long theaterId = 21L;
        Long movieId = 1L;
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(12).withMinute(10); // 내일 12:10

        Schedule schedule = scheduleService.createSchedule(theaterId, movieId, startTime);

        System.out.println("✅ 성공적으로 추가된 스케줄 ID: " + schedule.getScheduleId());
    }

    @Test
    void createSchedule_shouldFail_whenOverlapping() {
        Long theaterId = 21L;
        Long movieId = 1L;
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(10); // 내일 14:10 → 기존 14:00~15:00과 겹침

        Assertions.assertThrows(IllegalStateException.class, () -> {
            scheduleService.createSchedule(theaterId, movieId, startTime);
        });
    }
}
