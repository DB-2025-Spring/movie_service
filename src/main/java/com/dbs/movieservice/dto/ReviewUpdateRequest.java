package com.dbs.movieservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "리뷰 수정 요청 DTO")
public class ReviewUpdateRequest {

    @Schema(description = "수정할 별점", example = "4.0")
    private double starRating;

    @Schema(description = "수정할 리뷰 내용", example = "조금 지루했어요, 그래도 괜찮은 편!")
    private String contentDesc;
}
