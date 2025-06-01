package com.dbs.movieservice.config;

import com.dbs.movieservice.controller.dto.ErrorResponse;
import com.dbs.movieservice.config.exception.BusinessException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러
 * 애플리케이션 전반에서 발생하는 예외를 통합적으로 처리합니다.
 * 일시적으로 비활성화됨
 */
@Slf4j
// @RestControllerAdvice
public class GlobalExceptionHandlerDisabled extends ResponseEntityExceptionHandler {

    /**
     * BusinessException 처리
     * - 비즈니스 로직에서 발생하는 사용자 정의 예외
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, WebRequest request) {
        log.warn("BusinessException occurred: {} (errorCode: {})", e.getMessage(), e.getErrorCode());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(e.getHttpStatus().value())
                .error(e.getErrorCode())
                .message(e.getMessage())
                .path(getRequestPath(request))
                .build();
        
        return ResponseEntity.status(e.getHttpStatus()).body(errorResponse);
    }

    /**
     * IllegalArgumentException 처리
     * - 잘못된 매개변수가 전달된 경우
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        log.warn("IllegalArgumentException occurred: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(e.getMessage())
                .path(getRequestPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * IllegalStateException 처리
     * - 객체 상태가 메서드 호출에 적합하지 않은 경우
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e, WebRequest request) {
        log.warn("IllegalStateException occurred: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(e.getMessage())
                .path(getRequestPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * EntityNotFoundException 처리
     * - JPA에서 엔티티를 찾을 수 없는 경우
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e, WebRequest request) {
        log.warn("EntityNotFoundException occurred: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(e.getMessage())
                .path(getRequestPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * RuntimeException 처리
     * - 일반적인 런타임 예외
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e, WebRequest request) {
        log.error("RuntimeException occurred: {}", e.getMessage(), e);
        
        // 특정 메시지에 따른 HTTP 상태 결정
        HttpStatus status = determineHttpStatusFromMessage(e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(e.getMessage())
                .path(getRequestPath(request))
                .build();
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * BadCredentialsException 처리
     * - 인증 실패 (잘못된 자격 증명)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e, WebRequest request) {
        log.warn("BadCredentialsException occurred: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("잘못된 사용자명 또는 비밀번호입니다.")
                .path(getRequestPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * AuthenticationException 처리
     * - 인증 관련 예외
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e, WebRequest request) {
        log.warn("AuthenticationException occurred: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("인증에 실패했습니다.")
                .path(getRequestPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * MethodArgumentNotValidException 처리 (오버라이드)
     * - @Valid 검증 실패 시
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        
        log.warn("MethodArgumentNotValidException occurred: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("입력값 검증에 실패했습니다.")
                .details(errors)
                .path(getRequestPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * BindException 처리
     * - 데이터 바인딩 오류
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e, WebRequest request) {
        log.warn("BindException occurred: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Binding Failed")
                .message("데이터 바인딩에 실패했습니다.")
                .details(errors)
                .path(getRequestPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 모든 예외를 처리하는 최종 핸들러
     * - 위에서 처리되지 않은 모든 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e, WebRequest request) {
        log.error("Unexpected exception occurred: {}", e.getMessage(), e);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("서버 내부 오류가 발생했습니다.")
                .path(getRequestPath(request))
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 런타임 예외 메시지를 기반으로 HTTP 상태 결정
     */
    private HttpStatus determineHttpStatusFromMessage(String message) {
        if (message == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("not found") || lowerMessage.contains("찾을 수 없습니다")) {
            return HttpStatus.NOT_FOUND;
        } else if (lowerMessage.contains("already") || lowerMessage.contains("이미")) {
            return HttpStatus.CONFLICT;
        } else if (lowerMessage.contains("invalid") || lowerMessage.contains("잘못된")) {
            return HttpStatus.BAD_REQUEST;
        } else if (lowerMessage.contains("unauthorized") || lowerMessage.contains("권한")) {
            return HttpStatus.UNAUTHORIZED;
        } else if (lowerMessage.contains("forbidden") || lowerMessage.contains("금지")) {
            return HttpStatus.FORBIDDEN;
        }
        
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * WebRequest에서 안전하게 요청 경로를 추출
     */
    private String getRequestPath(WebRequest request) {
        String description = request.getDescription(false);
        if (description.startsWith("uri=")) {
            return description.substring(4);
        }
        return description;
    }
} 