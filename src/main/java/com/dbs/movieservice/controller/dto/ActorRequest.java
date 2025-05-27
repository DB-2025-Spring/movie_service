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
@Schema(description = "배우 정보 요청 DTO")
public class ActorRequest {
    
    @NotBlank
    @Size(min = 1, max = 50)
    @Schema(description = "배우명", example = "로버트 다우니 주니어", required = true)
    private String actorName;
} 