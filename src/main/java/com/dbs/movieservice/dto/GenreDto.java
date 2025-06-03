package com.dbs.movieservice.dto;

import com.dbs.movieservice.domain.movie.Genre;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "영화 장르 정보를 담는 DTO")
public class GenreDto {

    @Schema(description = "장르 ID", example = "1")
    private final Long genreId;

    @Schema(description = "장르 이름", example = "액션")
    private final String genreName;

    public GenreDto(Genre genre) {
        this.genreId = genre.getGenreId();
        this.genreName = genre.getGenreName();
    }
}
