package com.dbs.movieservice.service.theater;

import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.repository.theater.TheaterRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TheaterService {
    private final TheaterRepository theaterRepository;

    public TheaterService(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    @Transactional
    public Theater createTheater(Theater theater) {
        return theaterRepository.save(theater);
    }

    //리턴값에서 null일 경우, 해당 id의 상영관은 없음
    public Optional<Theater> getTheaterById(Long id) {
        return theaterRepository.findById(id);
    }


}
