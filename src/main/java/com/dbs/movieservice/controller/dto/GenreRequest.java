package com.dbs.movieservice.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "장르 정보 요청 DTO")
public class GenreRequest {
    
    @NotBlank
    @Size(min = 1, max = 100)
    @Schema(description = "장르명", example = "액션", required = true)
    private String genreName;
} 