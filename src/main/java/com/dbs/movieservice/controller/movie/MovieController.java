package com.dbs.movieservice.controller.movie;

import com.dbs.movieservice.dto.MovieDto;
import com.dbs.movieservice.service.movie.MovieService;
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
@RequestMapping("/api/movies")
@Tag(name = "영화 API", description = "영화 검색, 상세 조회, 차트 등 영화 관련 API")
public class MovieController {

    private final MovieService movieService;

    @Operation(
            summary = "키워드로 영화 검색",
            description = "영화 제목, 감독 이름, 배우 이름에 대해 키워드를 기반으로 영화를 검색합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<List<MovieDto>> searchMovies(
            @Parameter(description = "검색 키워드", example = "어벤져스")
            @RequestParam String keyword) {
        return ResponseEntity.ok(movieService.searchMoviesByKeyword(keyword));
    }

    @Operation(
            summary = "자동완성 (제목 기반)",
            description = "영화 제목을 기준으로 자동완성 추천 목록을 제공합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자동완성 목록 반환")
    })
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autocompleteTitles(
            @Parameter(description = "자동완성 키워드", example = "인셉")
            @RequestParam String keyword) {
        return ResponseEntity.ok(movieService.getAutoCompleteTitles(keyword));
    }

    @Operation(
            summary = "영화 상세 조회",
            description = "영화 ID를 기준으로 영화의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 영화를 찾을 수 없음")
    })
    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieDetail(
            @Parameter(description = "영화 ID", example = "10")
            @PathVariable Long movieId) {
        return ResponseEntity.ok(movieService.getMovieDetail(movieId));
    }

    @Operation(
            summary = "최신 영화 조회",
            description = "개봉일 기준 최신 영화들을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최신 영화 목록 반환")
    })
    @GetMapping("/recent")
    public ResponseEntity<List<MovieDto>> getRecentMovies() {
        return ResponseEntity.ok(movieService.getRecentMovies());
    }

    @Operation(
            summary = "현재 상영 중인 영화 조회",
            description = "현재 날짜 기준으로 상영 중인 영화 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현재 상영작 목록 반환")
    })
    @GetMapping("/now-showing")
    public ResponseEntity<List<MovieDto>> getNowShowingMovies() {
        return ResponseEntity.ok(movieService.getNowShowingMovies());
    }

    @Operation(
            summary = "상영 예정작 조회",
            description = "개봉 예정일이 미래인 영화 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예정작 목록 반환")
    })
    @GetMapping("/upcoming")
    public ResponseEntity<List<MovieDto>> getUpcomingMovies() {
        return ResponseEntity.ok(movieService.getUpcomingMovies());
    }
}
