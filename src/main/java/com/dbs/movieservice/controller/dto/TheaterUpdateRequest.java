package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상영관 수정 요청 DTO")
public class TheaterUpdateRequest {

    @NotBlank
    @Size(min = 1, max = 100)
    @Schema(description = "상영관명", example = "1관", required = true)
    private String theaterName;

    @NotNull
    @Positive
    @Schema(description = "행 수", example = "10", required = true)
    private Integer rows;

    @NotNull
    @Positive
    @Schema(description = "열 수", example = "15", required = true)
    private Integer columns;
}
