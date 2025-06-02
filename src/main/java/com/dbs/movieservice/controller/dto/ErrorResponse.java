package com.dbs.movieservice.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 에러 응답을 위한 DTO 클래스
 * 전역 예외 핸들러에서 사용됩니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    /**
     * HTTP 상태 코드
     */
    private int status;
    
    /**
     * 에러 타입
     */
    private String error;
    
    /**
     * 에러 메시지
     */
    private String message;
    
    /**
     * 요청 경로
     */
    private String path;
    
    /**
     * 상세 에러 정보 (예: 유효성 검증 실패 시 필드별 에러)
     */
    private Map<String, String> details;
    
    /**
     * 에러 발생 시간
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
} 