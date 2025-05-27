package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "영화 정보 요청 DTO")
public class MovieRequest {
    
    @NotBlank
    @Size(max = 10)
    @Schema(description = "관람등급", example = "12세", required = true)
    private String viewRating;
    
    @NotBlank
    @Size(min = 1, max = 100)
    @Schema(description = "영화제목", example = "어벤져스: 엔드게임", required = true)
    private String movieName;
    
    @NotNull
    @Positive
    @Schema(description = "상영시간(분)", example = "180", required = true)
    private Integer runningTime;
    
    @Size(max = 50)
    @Schema(description = "감독명", example = "안소니 루소")
    private String directorName;
    
    @Schema(description = "영화 설명", example = "마블 시네마틱 유니버스의 클라이맥스를 다룬 대작")
    private String movieDesc;
    
    @Size(max = 100)
    @Schema(description = "배급사", example = "월트 디즈니 컴퍼니 코리아")
    private String distributor;
    
    @Size(max = 500)
    @Schema(description = "이미지 URL", example = "https://example.com/poster.jpg")
    private String imageUrl;
    
    @NotNull
    @Schema(description = "개봉일", example = "2024-04-26", required = true)
    private LocalDate releaseDate;
    
    @Schema(description = "상영종료일", example = "2024-06-26")
    private LocalDate endDate;
    
    @Size(max = 100)
    @Schema(description = "제작국가", example = "미국")
    private String coo;
} 