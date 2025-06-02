package com.dbs.movieservice.service.theater;

import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.repository.theater.TheaterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheaterService {
    private final TheaterRepository theaterRepository;

    /**
     * 기존 메서드 - 상영관 생성
     */
    @Transactional
    public Theater createTheater(String theaterName) {
        Theater theater = new Theater();
        theater.setTheaterName(theaterName);
        theater.setTotalSeats(0);
        return theaterRepository.save(theater);
    }

    /**
     * 기존 메서드 - ID로 상영관 조회 (리턴값에서 null일 경우, 해당 id의 상영관은 없음)
     */
    public Optional<Theater> getTheaterById(Long id) {
        return theaterRepository.findById(id);
    }

    //todo theater delete - domain을 @Where(clause = "is_deleted = false")사용으로 수정?
    public Theater updateTheater(Theater theater) {
        return theaterRepository.save(theater);
    }
    public List<Theater> getAllTheaters(){
        return theaterRepository.findAll();
    }
    // ========== Admin용 추가 메서드들 ==========

    /**
     * 모든 상영관 조회
     */
    @Transactional(readOnly = true)
    public List<Theater> findAllTheaters() {
        return theaterRepository.findAll();
    }

    /**
     * ID로 상영관 조회 (Admin용 - 기존 메서드와 같은 기능)
     */
    @Transactional(readOnly = true)
    public Optional<Theater> findTheaterById(Long theaterId) {
        return theaterRepository.findById(theaterId);
    }

    /**
     * 상영관 저장 (Admin용 - createTheater와 같은 기능)
     */
    @Transactional
    public Theater saveTheater(Theater theater) {
        return theaterRepository.save(theater);
    }

    /**
     * 상영관 수정
     */
    @Transactional
    public Theater updateTheater(Long theaterId, String theaterName, Integer totalSeats) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("상영관을 찾을 수 없습니다: " + theaterId));

        theater.setTheaterName(theaterName);
        theater.setTotalSeats(totalSeats);

        return theaterRepository.save(theater);
    }

    /**
     * 상영관 삭제
     */
    @Transactional
    public void deleteTheater(Long theaterId) {
        if (!theaterRepository.existsById(theaterId)) {
            throw new RuntimeException("상영관을 찾을 수 없습니다: " + theaterId);
        }
        theaterRepository.deleteById(theaterId);
    }
}
