package com.dbs.movieservice.controller.movie;

import com.dbs.movieservice.dto.GenreDto;
import com.dbs.movieservice.dto.MovieDto;
import com.dbs.movieservice.service.movie.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
@Tag(name = "장르 API", description = "장르 조회 및 장르별 영화 목록 조회")
public class GenreController {

    private final GenreService genreService;

    /**
     * 전체 장르 목록 조회
     */
    @Operation(
            summary = "전체 장르 목록 조회",
            description = "시스템에 등록된 모든 장르를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장르 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<GenreDto>> getAllGenres() {
        return ResponseEntity.ok(genreService.findAllGenres());
    }

    /**
     * 특정 장르에 속한 영화들 조회
     */
    @Operation(
            summary = "특정 장르의 영화 조회",
            description = "장르 ID를 기준으로 해당 장르에 속한 영화들을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 장르 영화 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 장르를 찾을 수 없음")
    })
    @GetMapping("/{genreId}/movies")
    public ResponseEntity<List<MovieDto>> getMoviesByGenre(
            @Parameter(description = "장르 ID", example = "3")
            @PathVariable Long genreId) {
        return ResponseEntity.ok(genreService.findMoviesByGenre(genreId));
    }
    /* 특정 영화에 대한 장르 조회*/
}
