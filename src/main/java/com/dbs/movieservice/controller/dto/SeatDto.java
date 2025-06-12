package com.dbs.movieservice.controller.dto;

import com.dbs.movieservice.domain.theater.Seat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "좌석 정보 응답 DTO")
public class SeatDto {
    
    @Schema(description = "좌석 ID", example = "1")
    private Long seatId;
    
    @Schema(description = "상영관 ID", example = "1")
    private Long theaterId;
    
    @Schema(description = "상영관 이름", example = "1관")
    private String theaterName;
    
    @Schema(description = "행 번호", example = "5")
    private Integer rowNumber;
    
    @Schema(description = "열 번호", example = "10")
    private Integer columnNumber;
    
    // Entity -> DTO 변환 메서드
    public static SeatDto from(Seat seat) {
        return new SeatDto(
            seat.getSeatId(),
            seat.getTheater().getTheaterId(),
            seat.getTheater().getTheaterName(),
            seat.getRowNumber(),
            seat.getColumnNumber()
        );
    }
} 