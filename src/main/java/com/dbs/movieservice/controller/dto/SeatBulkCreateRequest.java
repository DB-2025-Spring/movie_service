package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "좌석 일괄 생성 요청 DTO")
public class SeatBulkCreateRequest {
    
    @NotNull
    @Positive
    @Schema(description = "상영관 ID", example = "1", required = true)
    private Long theaterId;
    
    @NotEmpty
    @Schema(description = "생성할 좌석 정보 목록", required = true)
    private List<SeatInfo> seats;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "좌석 정보")
    public static class SeatInfo {
        @NotNull
        @Positive
        @Schema(description = "행 번호", example = "5", required = true)
        private Integer rowNumber;
        
        @NotNull
        @Positive
        @Schema(description = "열 번호", example = "10", required = true)
        private Integer columnNumber;
    }
} 