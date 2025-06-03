package com.dbs.movieservice.controller.movie;

import com.dbs.movieservice.dto.ReviewCreateRequest;
import com.dbs.movieservice.dto.ReviewDto;
import com.dbs.movieservice.dto.ReviewUpdateRequest;
import com.dbs.movieservice.service.movie.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/reviews")
@Tag(name = "리뷰 API", description = "영화 리뷰 작성, 조회, 수정, 삭제")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(
            summary = "특정 영화의 리뷰 목록 조회",
            description = "해당 영화 ID에 대한 리뷰 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공")
    })
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByMovie(
            @Parameter(description = "영화 ID", example = "1")
            @PathVariable Long movieId) {
        return ResponseEntity.ok(reviewService.getReviewsByMovie(movieId));
    }

    @Operation(
            summary = "리뷰 작성",
            description = "사용자가 특정 영화에 대해 리뷰를 작성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
    })
    @PostMapping
    public ResponseEntity<ReviewDto> writeReview(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "리뷰 작성 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ReviewCreateRequest.class))
            )
            @RequestBody ReviewCreateRequest request) {
        ReviewDto review = reviewService.createReview(request);
        return ResponseEntity.ok(review);
    }

    @Operation(
            summary = "리뷰 수정",
            description = "리뷰 ID에 해당하는 리뷰를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 리뷰를 찾을 수 없음")
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(
            @Parameter(description = "리뷰 ID", example = "101")
            @PathVariable Long reviewId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "리뷰 수정 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ReviewUpdateRequest.class))
            )
            @RequestBody ReviewUpdateRequest request) {
        ReviewDto updated = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "리뷰 삭제",
            description = "리뷰 ID를 기준으로 리뷰를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 리뷰를 찾을 수 없음")
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "리뷰 ID", example = "101")
            @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "마이페이지 - 내가 작성한 리뷰 목록 조회",
            description = "로그인한 사용자의 ID를 기준으로 본인이 작성한 리뷰 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 리뷰 목록 조회 성공")
    })
    @GetMapping("/my")
    public ResponseEntity<List<ReviewDto>> getMyReviews(
            @Parameter(description = "고객 ID", example = "42")
            @RequestParam Long customerId) {
        return ResponseEntity.ok(reviewService.getReviewsByCustomer(customerId));
    }
}
