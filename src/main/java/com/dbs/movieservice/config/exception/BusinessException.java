package com.dbs.movieservice.config.exception;

import org.springframework.http.HttpStatus;

/**
 * 비즈니스 로직에서 발생하는 예외를 위한 사용자 정의 예외 클래스
 */
public class BusinessException extends RuntimeException {
    
    private final HttpStatus httpStatus;
    private final String errorCode;
    
    public BusinessException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.errorCode = "BUSINESS_ERROR";
    }
    
    public BusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = "BUSINESS_ERROR";
    }
    
    public BusinessException(String message, String errorCode) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.errorCode = errorCode;
    }
    
    public BusinessException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
} 