package com.dbs.movieservice.dto;

import com.dbs.movieservice.domain.movie.Movie;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Schema(description = "영화 정보를 담는 DTO")
public class MovieDto {

    @Schema(description = "영화 ID", example = "101")
    private final Long movieId;

    @Schema(description = "영화 제목", example = "인터스텔라")
    private final String title;

    @Schema(description = "감독 이름", example = "크리스토퍼 놀란")
    private final String director;

    @Schema(description = "영화 포스터 이미지 URL", example = "https://image.example.com/interstellar.jpg")
    private final String imageUrl;

    @Schema(description = "영화 설명", example = "우주를 배경으로 한 감동적인 SF 영화")
    private final String description;

    @Schema(description = "관람 등급", example = "12세 이상 관람가")
    private final String viewRating;

    @Schema(description = "개봉일", example = "2023-11-01")
    private final LocalDate releaseDate;

    @Schema(description = "러닝타임 (분)", example = "169")
    private final Integer runningTime;

    public MovieDto(Movie movie) {
        this.movieId = movie.getMovieId();
        this.title = movie.getMovieName();
        this.director = movie.getDirectorName();
        this.imageUrl = movie.getImageUrl();
        this.description = movie.getMovieDesc();
        this.viewRating = movie.getViewRating();
        this.releaseDate = movie.getReleaseDate();
        this.runningTime = movie.getRunningTime();
    }
}
