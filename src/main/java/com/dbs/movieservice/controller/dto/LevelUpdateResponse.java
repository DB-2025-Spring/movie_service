package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 등급 업데이트 응답을 위한 DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "등급 업데이트 응답")
public class LevelUpdateResponse {
    
    @Schema(description = "등급업 발생 여부", example = "true")
    private boolean levelUpOccurred;
    
    @Schema(description = "사용자 ID", example = "testuser123")
    private String customerInputId;
    
    @Schema(description = "티켓 수량", example = "5")
    private int ticketCount;
    
    @Schema(description = "응답 메시지", example = "등급이 업그레이드되었습니다!")
    private String message;
} 