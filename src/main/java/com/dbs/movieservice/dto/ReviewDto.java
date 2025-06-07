package com.dbs.movieservice.dto;

import com.dbs.movieservice.domain.movie.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Schema(description = "리뷰 응답 DTO")
public class ReviewDto {

    @Schema(description = "리뷰 ID", example = "301")
    private final Long reviewId;

    @Schema(description = "영화 ID", example = "101")
    private final Long movieId;

    @Schema(description = "영화 이름", example = "인셉션")
    private final String movieName;

    @Schema(description = "고객 ID", example = "501")
    private final Long customerId;

    @Schema(description = "고객 이름", example = "홍길동")
    private final String customerName;

    @Schema(description = "별점", example = "4.5")
    private final double starRating;

    @Schema(description = "리뷰 내용", example = "완전 감동적이었어요!")
    private final String contentDesc;

    @Schema(description = "작성일", example = "2025-06-01")
    private final LocalDate dateCreated;

    public ReviewDto(Review review) {
        this.reviewId = review.getReviewId();
        this.movieId = review.getMovie().getMovieId();
        this.movieName = review.getMovie().getMovieName();
        this.customerId = review.getCustomer().getCustomerId();
        this.customerName = review.getCustomer().getCustomerName();
        this.starRating = review.getStarRating();
        this.contentDesc = review.getContentDesc();
        this.dateCreated = review.getDateCreated();
    }
}
