package com.dbs.movieservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "리뷰 작성 요청 DTO")
public class ReviewCreateRequest {

    @Schema(description = "영화 ID", example = "101")
    private Long movieId;

    @Schema(description = "리뷰 작성자(고객) ID", example = "501")
    private Long customerId;

    @Schema(description = "별점 (0.5 ~ 5.0)", example = "4.5")
    private double starRating;

    @Schema(description = "리뷰 내용", example = "정말 감동적인 영화였어요. OST도 최고!")
    private String contentDesc;
}
