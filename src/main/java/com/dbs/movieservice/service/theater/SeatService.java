package com.dbs.movieservice.service.theater;

import com.dbs.movieservice.controller.dto.SeatBulkCreateRequest;
import com.dbs.movieservice.domain.theater.Seat;
import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.repository.theater.SeatAvailableRepository;
import com.dbs.movieservice.repository.theater.SeatRepository;
import com.dbs.movieservice.repository.theater.TheaterRepository;
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
public class SeatService {
    private final SeatRepository seatRepository;
    private final TheaterRepository theaterRepository;
    private final SeatAvailableRepository seatAvailableRepository;

    /**
     * 기존 메서드 - seat의 생성자. 이때, 상영관이 먼저 존재하는지 부터 확인함.
     */
    @Transactional
    public Seat createSeat(Seat seat) {
        Long theaterId = seat.getTheater().getTheaterId();
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new EntityNotFoundException("상영관이 존재하지 않습니다."));
        seatRepository.save(seat);
        theater.setTotalSeats(theater.getTotalSeats() + 1); //save를 따로 호출 하지 않는 이유는, 이미 영속 상태 객체이기 떄문에 할 필요없음.
        return seat;
    }

    // ========== Admin용 추가 메서드들 ==========

    /**
     * 모든 좌석 조회
     */
    @Transactional(readOnly = true)
    public List<Seat> findAllSeats() {
        return seatRepository.findAll();
    }

    /**
     * ID로 좌석 조회
     */
    @Transactional(readOnly = true)
    public Optional<Seat> findSeatById(Long seatId) {
        return seatRepository.findById(seatId);
    }

    /**
     * 좌석 저장 (Admin용)
     */
    @Transactional
    public Seat saveSeat(Long theaterId, Integer rowNumber, Integer columnNumber) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("상영관을 찾을 수 없습니다: " + theaterId));
        
        Seat seat = new Seat();
        seat.setTheater(theater);
        seat.setRowNumber(rowNumber);
        seat.setColumnNumber(columnNumber);
        
        Seat savedSeat = seatRepository.save(seat);
        theater.setTotalSeats(theater.getTotalSeats() + 1);
        return savedSeat;
    }

    /**
     * 좌석 수정
     */
    @Transactional
    public Seat updateSeat(Long seatId, Long theaterId, Integer rowNumber, Integer columnNumber) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("좌석을 찾을 수 없습니다: " + seatId));
        
        Theater oldTheater = seat.getTheater();
        Theater newTheater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("상영관을 찾을 수 없습니다: " + theaterId));
        
        // 상영관이 변경되는 경우 좌석 수 업데이트
        if (!oldTheater.getTheaterId().equals(newTheater.getTheaterId())) {
            oldTheater.setTotalSeats(oldTheater.getTotalSeats() - 1);
            newTheater.setTotalSeats(newTheater.getTotalSeats() + 1);
        }
        
        seat.setTheater(newTheater);
        seat.setRowNumber(rowNumber);
        seat.setColumnNumber(columnNumber);
        
        return seatRepository.save(seat);
    }

    /**
     * 좌석 삭제
     */
    @Transactional
    public void deleteSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("좌석을 찾을 수 없습니다: " + seatId));

        // 🔥 먼저 seatAvailable 제거
        seatAvailableRepository.deleteAllBySeatId(seat.getSeatId());

        // ✅ 연결된 theater의 총 좌석수 감소
        Theater theater = seat.getTheater();
        theater.setTotalSeats(theater.getTotalSeats() - 1);

        // ✅ 좌석 제거
        seatRepository.deleteById(seatId);
    }

    @Transactional
    public void deleteSeats(List<Long> seatIds) {
        seatAvailableRepository.deleteAllBySeatIds(seatIds);
        seatRepository.deleteAllById(seatIds);
    }


    public Seat parseColRowToId(Theater theater, Integer rowNumber, Integer columnNumber) {
        return seatRepository.findByTheaterAndRowNumberAndColumnNumber(theater,rowNumber,columnNumber);
    }

    public List<Seat> praseIdstoColRow(List<Seat> seat){
        List<Seat> seats = new ArrayList<>();
        for(Seat s : seat){
            Seat tempSeat = seatRepository.findById(s.getSeatId()).orElse(null);
            if(tempSeat != null){
                seats.add(tempSeat);
            }
        }
        return seats;
    }
    
    // ========== 좌석 일괄 관리 메서드들 ==========
    
    /**
     * 좌석 일괄 생성 (특정 상영관)
     */
    @Transactional
    public List<Seat> createSeatsInBulk(Long theaterId, List<SeatBulkCreateRequest.SeatInfo> seatInfos) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("상영관을 찾을 수 없습니다: " + theaterId));
        
        List<Seat> seats = new ArrayList<>();
        for (SeatBulkCreateRequest.SeatInfo seatInfo : seatInfos) {
            // 중복 좌석 체크
            Seat existingSeat = seatRepository.findByTheaterAndRowNumberAndColumnNumber(
                theater, seatInfo.getRowNumber(), seatInfo.getColumnNumber()
            );
            if (existingSeat != null) {
                throw new RuntimeException(
                    String.format("좌석이 이미 존재합니다: %d행 %d열", 
                        seatInfo.getRowNumber(), seatInfo.getColumnNumber())
                );
            }
            
            Seat seat = new Seat();
            seat.setTheater(theater);
            seat.setRowNumber(seatInfo.getRowNumber());
            seat.setColumnNumber(seatInfo.getColumnNumber());
            seats.add(seat);
        }
        
        List<Seat> savedSeats = seatRepository.saveAll(seats);
        
        // 상영관 총 좌석 수 업데이트
        theater.setTotalSeats(theater.getTotalSeats() + savedSeats.size());
        
        return savedSeats;
    }
    
    /**
     * 특정 상영관의 모든 좌석 조회
     */
    @Transactional(readOnly = true)
    public List<Seat> findSeatsByTheaterId(Long theaterId) {
        return seatRepository.findByTheater_TheaterId(theaterId);
    }
    
    /**
     * 특정 상영관의 좌석 수 조회
     */
    @Transactional(readOnly = true)
    public int countSeatsByTheaterId(Long theaterId) {
        return seatRepository.countByTheater_TheaterId(theaterId);
    }
    
    /**
     * 특정 상영관의 모든 좌석 삭제
     */
    @Transactional
    public void deleteAllSeatsByTheaterId(Long theaterId) {
        List<Seat> seats = seatRepository.findByTheater_TheaterId(theaterId);
        seatRepository.deleteAll(seats);
        
        // 상영관 총 좌석 수 업데이트
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("상영관을 찾을 수 없습니다: " + theaterId));
        theater.setTotalSeats(0);
    }
}
