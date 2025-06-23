package com.dbs.movieservice.service.theater;

import com.dbs.movieservice.controller.dto.ScheduleResponse;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.theater.Schedule;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.repository.movie.MovieRepository;
import com.dbs.movieservice.repository.theater.ScheduleRepository;
import com.dbs.movieservice.repository.theater.SeatAvailableRepository;
import com.dbs.movieservice.repository.theater.TheaterRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Future;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final TheaterRepository theaterRepository;
    private final MovieRepository movieRepository;
    private final SeatAvailableService seatAvailableService;
    private final SeatService seatService;
    private final SeatAvailableRepository seatAvailableRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, TheaterRepository theaterRepository, MovieRepository movieRepository, SeatAvailableService seatAvailableService, SeatService seatService, SeatAvailableRepository seatAvailableRepository) {
        this.scheduleRepository = scheduleRepository;
        this.theaterRepository = theaterRepository;
        this.movieRepository = movieRepository;
        this.seatAvailableService = seatAvailableService;
        this.seatService = seatService;
        this.seatAvailableRepository = seatAvailableRepository;
    }

    //이 메소드는 아직, 다음날에 있는 상영일정은 고려 안함. 추후 수정하겠습니다.
    @Transactional
    public Schedule createSchedule(Long theaterId, Long movieId, LocalDateTime startTime, @Future(message = "마감 시간은 현재보다 이후여야 합니다.") LocalDateTime scheduleEndTime) {
        LocalDate date = startTime.toLocalDate();

        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new EntityNotFoundException("상영관이 존재하지 않습니다."));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("영화가 존재하지 않습니다."));

        LocalDateTime endTime = startTime.plusMinutes(movie.getRunningTime());

        List<Schedule> schedulesOnSameDate = scheduleRepository.findByScheduleDateAndTheater_TheaterId(date, theaterId);
        boolean overlaps = schedulesOnSameDate.stream().anyMatch(otherSchedule -> {
            LocalDateTime otherScheduleStartTime = otherSchedule.getScheduleStartTime();
            LocalDateTime otherScheduleEndTime = otherSchedule.getScheduleEndTime();

            return !(endTime.isBefore(otherScheduleStartTime) || startTime.isAfter(otherScheduleEndTime));
        });

        if (overlaps) {
            throw new IllegalStateException("해당 시간에 상영 중인 영화가 이미 존재합니다.");
        }
        //추가하려는 영화의 순서를 찾음.
        long sequence = schedulesOnSameDate.stream()
                .filter(s -> s.getScheduleStartTime().isBefore(startTime))
                .count() + 1;
        //추가하려는 영화보다 늦게 시작하는 영화의 순서를 뒤로 미룸.
        schedulesOnSameDate.stream()
                .filter(s -> s.getScheduleStartTime().isAfter(startTime))
                .forEach(s -> s.setScheduleSequence(s.getScheduleSequence() + 1));

        Schedule schedule = new Schedule();
        schedule.setTheater(theater);
        schedule.setMovie(movie);
        schedule.setScheduleDate(date);
        schedule.setScheduleStartTime(startTime);
        schedule.setScheduleEndTime(endTime);
        schedule.setScheduleSequence((int) sequence);

        Schedule cSchedule = scheduleRepository.save(schedule);
        seatAvailableService.createSeatAvailableForSchedule(cSchedule);
        return cSchedule;
    }

    //cgv를 보니, 해당날자 기준, 방영한 영화를 포함해서 다 조회하기에, 금일 기준으로 조회하게 정의했습니다.
    public List<Schedule> getSchedulesForNext7Days(Long movieId) {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfTargetDay = startOfToday.plusDays(7);

        return scheduleRepository.findSchedulesForNext7Days(movieId, startOfToday, endOfTargetDay);
    }



    /**
     *
     * @param selectedDate
     * @param movieId
     * @return Map<Schedule, LONG>
     */
    public Map<Schedule, Long> getSchedulesFor1Day(LocalDate selectedDate,Long movieId) {
        List<Schedule> schedulesList =scheduleRepository.findByScheduleDateAndMovie_MovieId(selectedDate,movieId);
        List<Long> scheduleIds = schedulesList.stream().map(Schedule::getScheduleId).toList();
        Map<Long, Long> countMap = seatAvailableService.countAvailableSeatMap(scheduleIds);
        // scheduleId → Schedule 객체 매핑용 Map
        Map<Long, Schedule> scheduleMap = schedulesList.stream()
                .collect(Collectors.toMap(Schedule::getScheduleId, s -> s));
        // 최종 Map<Schedule, Long> 생성
        return countMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> scheduleMap.get(entry.getKey()),
                        Map.Entry::getValue
                ));
    }

    //=====v2용 메서드============
    /**
     * scheduleController V2용 함수. 오늘을 기점으로, 15일간 영화가 있는날을 리스트로 반환한다.
     * @param
     * @return
     */
    public List<LocalDate> getScheduledDatesForNext15Days() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(15);

        List<Object> result = scheduleRepository.findScheduleDateBetweenTwoDateNative(start, end);
        return result.stream()
                .map(val -> ((Timestamp) val).toLocalDateTime().toLocalDate())
                .distinct()
                .toList();
    }

    /**
     * 날짜를 하나 전달 받으면 movie를 left join한 schedule List를 반환받늗다.
     * @param date
     * @return
     */
    public List<Schedule> getScheduleByDate(LocalDate date) {
        return scheduleRepository.findSchedulesByDate(date);
    }
    // ========== Admin용 추가 메서드들 ==========

    /**
     * 모든 상영일정 조회 (DTO 반환)
     */
    @Transactional(readOnly = true)
    public List<ScheduleResponse> findAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        return schedules.stream()
                .map(schedule -> {
                    // 연관 엔티티 정보를 명시적으로 로딩
                    schedule.getMovie().getMovieName(); // lazy loading 초기화
                    schedule.getTheater().getTheaterName(); // lazy loading 초기화
                    return ScheduleResponse.from(schedule);
                })
                .toList();
    }

    /**
     * ID로 상영일정 조회
     */
    @Transactional(readOnly = true)
    public Optional<Schedule> findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }

    /**
     * 상영일정 저장 (Admin용)
     */
    @Transactional
    public Schedule saveSchedule(Long movieId, Long theaterId, LocalDate scheduleDate, 
                               Integer scheduleSequence, LocalDateTime scheduleStartTime) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("영화를 찾을 수 없습니다: " + movieId));
        
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("상영관을 찾을 수 없습니다: " + theaterId));
        
        Schedule schedule = new Schedule();
        schedule.setMovie(movie);
        schedule.setTheater(theater);
        schedule.setScheduleDate(scheduleDate);
        schedule.setScheduleSequence(scheduleSequence);
        schedule.setScheduleStartTime(scheduleStartTime);
        // endTime 계산
        LocalDateTime endTime = scheduleStartTime.plusMinutes(movie.getRunningTime());
        schedule.setScheduleEndTime(endTime);
        
        Schedule savedSchedule = scheduleRepository.save(schedule);
        seatAvailableService.createSeatAvailableForSchedule(savedSchedule);
        return savedSchedule;
    }

    /**
     * 상영일정 수정
     */
    @Transactional
    public Schedule updateSchedule(Long scheduleId, Long movieId, Long theaterId,
                                   LocalDateTime scheduleStartTime, @Future(message = "마감 시간은 현재보다 이후여야 합니다.") LocalDateTime scheduleEndTime) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("상영일정을 찾을 수 없습니다: " + scheduleId));
        
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("영화를 찾을 수 없습니다: " + movieId));
        
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("상영관을 찾을 수 없습니다: " + theaterId));
        
        schedule.setMovie(movie);
        schedule.setTheater(theater);
        schedule.setScheduleStartTime(scheduleStartTime);
        // endTime 계산
        LocalDateTime endTime = scheduleStartTime.plusMinutes(movie.getRunningTime());
        schedule.setScheduleEndTime(endTime);
        
        return scheduleRepository.save(schedule);
    }

    /**
     * 상영일정 삭제
     */
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new RuntimeException("상영일정을 찾을 수 없습니다: " + scheduleId);
        }
        seatAvailableRepository.deleteAllBySchedule(scheduleId);
        scheduleRepository.deleteById(scheduleId);
    }

    public List<Long> parseSeatBySchedule(Schedule schedule, List<Seat> seats) {
        Schedule getSchedule = scheduleRepository.findById(schedule.getScheduleId()).orElseThrow();
        Theater theater = getSchedule.getTheater();
        List<Long> seatsId = new ArrayList<>();
        for (Seat seat : seats) {
            seatsId.add(seatService.parseColRowToId(theater, seat.getRowNumber(), seat.getColumnNumber()).getSeatId());
        }
        return seatsId;
    }
}


