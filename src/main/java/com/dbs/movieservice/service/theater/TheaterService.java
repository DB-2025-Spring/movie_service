package com.dbs.movieservice.service.theater;

import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.repository.theater.TheaterRepository;
import com.dbs.movieservice.repository.theater.SeatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheaterService {
    private final TheaterRepository theaterRepository;
    private final SeatRepository seatRepository;



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
     * 상영관 수정
     */
    @Transactional
    public Theater updateTheater(Long id, String name, int rows, int columns) {
        Theater theater = theaterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Theater not found"));

        theater.setTheaterName(name);
        theater.setRows(rows);
        theater.setColumns(columns);
        theater.setTotalSeats(rows * columns);

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
    
    /**
     * 상영관과 좌석을 함께 생성 - 권장 방법
     */
    @Transactional
    public Theater createTheaterWithSeats(String theaterName, Integer rows, Integer columns) {
        // 1. 상영관 생성
        Theater theater = new Theater();
        theater.setTheaterName(theaterName);
        theater.setTotalSeats(0); // 초기값
        Theater savedTheater = theaterRepository.save(theater);
        
        // 2. 좌석 일괄 생성
        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= columns; col++) {
                Seat seat = new Seat();
                seat.setTheater(savedTheater);
                seat.setRowNumber(row);
                seat.setColumnNumber(col);
                seats.add(seat);
            }
        }
        
        // 3. 좌석 저장
        seatRepository.saveAll(seats);
        
        // 4. 총 좌석 수 업데이트
        savedTheater.setTotalSeats(seats.size());
        
        return savedTheater;
    }
    
    /**
     * 상영관과 지정된 개수의 좌석을 함께 생성
     */
    @Transactional
    public Theater createTheaterWithLimitedSeats(String theaterName, Integer rows, Integer columns, Integer totalSeats) {
        // 최대 좌석수 검증
        int maxPossibleSeats = rows * columns;
        if (totalSeats > maxPossibleSeats) {
            throw new RuntimeException(
                String.format("총 좌석수(%d)는 레이아웃 크기(%dx%d=%d)를 초과할 수 없습니다.", 
                    totalSeats, rows, columns, maxPossibleSeats)
            );
        }
        
        // 1. 상영관 생성
        Theater theater = new Theater();
        theater.setTheaterName(theaterName);
        theater.setTotalSeats(0); // 초기값
        theater.setRows(rows);
        theater.setColumns(columns);
        Theater savedTheater = theaterRepository.save(theater);
        
        // 2. 지정된 개수만큼 좌석 생성 (앞쪽부터 순서대로)
        List<Seat> seats = new ArrayList<>();
        int createdSeats = 0;
        
        outerLoop:
        for (int row = 1; row <= rows && createdSeats < totalSeats; row++) {
            for (int col = 1; col <= columns && createdSeats < totalSeats; col++) {
                Seat seat = new Seat();
                seat.setTheater(savedTheater);
                seat.setRowNumber(row);
                seat.setColumnNumber(col);
                seats.add(seat);
                createdSeats++;
            }
        }
        
        // 3. 좌석 저장
        seatRepository.saveAll(seats);
        
        // 4. 총 좌석 수 업데이트
        savedTheater.setTotalSeats(seats.size());
        
        return savedTheater;
    }
    
    /**
     * 상영관 좌석 수 재계산 및 업데이트
     */
    @Transactional
    public Theater recalculateTotalSeats(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("상영관을 찾을 수 없습니다: " + theaterId));
                
        int actualSeatCount = seatRepository.countByTheater_TheaterId(theaterId);
        theater.setTotalSeats(actualSeatCount);
        
        return theater;
    }
}
