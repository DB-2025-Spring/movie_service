package com.dbs.movieservice.controller.movie;

import com.dbs.movieservice.dto.ActorDto;
import com.dbs.movieservice.dto.MovieDto;
import com.dbs.movieservice.service.movie.ActorService;
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
@RequestMapping("api/actors")
@Tag(name = "배우 API", description = "배우 관련 기능 제공")
public class ActorController {

    private final ActorService actorService;

    /**
     * 배우 이름으로 검색
     */
    @Operation(
            summary = "배우 이름으로 검색",
            description = "이름으로 배우를 검색합니다. 동명이인이 있을 수 있으므로 여러 명이 반환될 수 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 누락되었거나 잘못되었습니다")
    })
    @GetMapping("/search")
    public ResponseEntity<List<ActorDto>> searchActors(
            @Parameter(description = "검색할 배우 이름", example = "하정우")
            @RequestParam String name) {
        return ResponseEntity.ok(actorService.searchActorsByName(name));
    }

    /**
     * 해당 배우의 출연 영화 목록 조회
     */
    @Operation(
            summary = "해당 배우의 출연 영화 목록 조회",
            description = "배우 ID를 기준으로 해당 배우가 출연한 영화 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "영화 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "배우를 찾을 수 없음")
    })
    @GetMapping("/{actorId}/movies")
    public ResponseEntity<List<MovieDto>> getMoviesByActor(
            @Parameter(description = "배우 ID", example = "12")
            @PathVariable Long actorId) {
        return ResponseEntity.ok(actorService.findMoviesByActor(actorId));
    }
}
