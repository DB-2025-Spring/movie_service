package com.dbs.movieservice.service.theater;

import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.repository.theater.SeatRepository;
import com.dbs.movieservice.repository.theater.TheaterRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;
    private final TheaterRepository theaterRepository;
    public SeatService(SeatRepository seatRepository, TheaterRepository theaterRepository) {
        this.seatRepository = seatRepository;
        this.theaterRepository = theaterRepository;
    }

    //seat의 생성자. 이때, 상영관이 먼저 존재하는지 부터 확인함.
    @Transactional
    public Seat createSeat(Seat seat) {
        Long theaterId = seat.getTheater().getTheaterId();
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new EntityNotFoundException("상영관이 존재하지 않습니다."));
        seatRepository.save(seat);
        theater.setTotalSeats(theater.getTotalSeats() + 1); //save를 따로 호출 하지 않는 이유는, 이미 영속 상태 객체이기 떄문에 할 필요없음.
        return seat;
    }

    //todo: Seat update? Seat 삭제?...
}
