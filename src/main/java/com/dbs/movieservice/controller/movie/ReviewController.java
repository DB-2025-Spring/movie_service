package com.dbs.movieservice.controller.movie;

import com.dbs.movieservice.dto.ReviewCreateRequest;
import com.dbs.movieservice.dto.ReviewDto;
import com.dbs.movieservice.dto.ReviewUpdateRequest;
import com.dbs.movieservice.service.movie.ReviewService;
import com.dbs.movieservice.service.member.CustomerService;
import com.dbs.movieservice.service.ticketing.TicketService;
import com.dbs.movieservice.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Tag(name = "리뷰 API", description = "영화 리뷰 작성, 조회, 수정, 삭제")
public class ReviewController {

    private final ReviewService reviewService;
    private final CustomerService customerService;
    private final TicketService ticketService;

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
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> writeReview(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "리뷰 작성 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ReviewCreateRequest.class))
            )
            @RequestBody ReviewCreateRequest request) {
        try {
            // 현재 로그인한 사용자 정보 가져오기
            String customerInputId = SecurityUtils.getCurrentCustomerInputId();
            Long currentCustomerId = customerService.getCustomerByInputId(customerInputId).getCustomerId();
            
            // 요청한 고객 ID와 현재 로그인한 고객 ID 일치 확인
            if (!currentCustomerId.equals(request.getCustomerId())) {
                return ResponseEntity.badRequest().body("본인의 리뷰만 작성할 수 있습니다.");
            }
            
            // 해당 영화를 실제로 관람했는지 확인 (결제완료 + 상영일지남)
            boolean hasWatchedMovie = ticketService.hasWatchedMovie(currentCustomerId, request.getMovieId());
            if (!hasWatchedMovie) {
                return ResponseEntity.badRequest().body("관람한 영화에 대해서만 리뷰를 작성할 수 있습니다.");
            }
            
            // 이미 해당 영화에 대한 리뷰가 있는지 확인 (영화당 1개 리뷰만)
            if (reviewService.findReviewByCustomerAndMovie(currentCustomerId, request.getMovieId()) != null) {
                return ResponseEntity.badRequest().body("이미 해당 영화에 대한 리뷰를 작성하셨습니다.");
            }
            
            ReviewDto review = reviewService.createReview(request);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("리뷰 작성에 실패했습니다: " + e.getMessage());
        }
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
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateReview(
            @Parameter(description = "리뷰 ID", example = "101")
            @PathVariable Long reviewId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "리뷰 수정 요청 바디",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ReviewUpdateRequest.class))
            )
            @RequestBody ReviewUpdateRequest request) {
        try {
            // 현재 로그인한 사용자 정보 가져오기
            String customerInputId = SecurityUtils.getCurrentCustomerInputId();
            Long currentCustomerId = customerService.getCustomerByInputId(customerInputId).getCustomerId();
            
            // 리뷰 소유자 확인
            if (!reviewService.isReviewOwner(reviewId, currentCustomerId)) {
                return ResponseEntity.badRequest().body("본인의 리뷰만 수정할 수 있습니다.");
            }
            
            ReviewDto updated = reviewService.updateReview(reviewId, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("리뷰 수정에 실패했습니다: " + e.getMessage());
        }
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
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteReview(
            @Parameter(description = "리뷰 ID", example = "101")
            @PathVariable Long reviewId) {
        try {
            // 현재 로그인한 사용자 정보 가져오기
            String customerInputId = SecurityUtils.getCurrentCustomerInputId();
            Long currentCustomerId = customerService.getCustomerByInputId(customerInputId).getCustomerId();
            
            // 리뷰 소유자 확인
            if (!reviewService.isReviewOwner(reviewId, currentCustomerId)) {
                return ResponseEntity.badRequest().body("본인의 리뷰만 삭제할 수 있습니다.");
            }
            
            reviewService.deleteReview(reviewId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("리뷰 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    @Operation(
            summary = "마이페이지 - 내가 작성한 리뷰 목록 조회",
            description = "로그인한 사용자의 JWT 토큰을 기준으로 본인이 작성한 리뷰 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 리뷰 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/my")
    public ResponseEntity<List<ReviewDto>> getMyReviews() {
        String customerInputId = SecurityUtils.getCurrentCustomerInputId();
        // customerInputId로 Customer 객체를 찾아서 customerId 추출
        Long customerId = customerService.getCustomerByInputId(customerInputId).getCustomerId();
        return ResponseEntity.ok(reviewService.getReviewsByCustomer(customerId));
    }
}
