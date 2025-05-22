package com.dbs.movieservice.service.theater;

import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.repository.movie.MovieRepository;
import com.dbs.movieservice.repository.theater.ScheduleRepository;
import com.dbs.movieservice.repository.theater.SeatRepository;
import com.dbs.movieservice.repository.theater.TheaterRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final TheaterRepository theaterRepository;
    private final MovieRepository movieRepository;
    private final SeatRepository seatRepository;
    public ScheduleService(ScheduleRepository scheduleRepository, TheaterRepository theaterRepository, MovieRepository movieRepository, SeatRepository seatRepository) {
        this.scheduleRepository = scheduleRepository;
        this.theaterRepository = theaterRepository;
        this.movieRepository = movieRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public void createSchedule(Long theaterId, Long movieId, LocalDateTime startTime) {
        LocalDate date = startTime.toLocalDate();
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new EntityNotFoundException("상영관이 존재하지 않습니다."));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("영화가 존재하지 않습니다."));

        List<Schedule> schedulesOnSameDate = scheduleRepository.findByScheduleDate(date);

    }

}
