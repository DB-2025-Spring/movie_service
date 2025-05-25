package com.dbs.movieservice.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieRequest {
    
    @NotBlank
    @Size(max = 10)
    private String viewRating;
    
    @NotBlank
    @Size(min = 1, max = 100)
    private String movieName;
    
    @NotNull
    @Positive
    private Integer runningTime;
    
    @Size(max = 50)
    private String directorName;
    
    private String movieDesc;
    
    @Size(max = 100)
    private String distributor;
    
    @Size(max = 500)
    private String imageUrl;
    
    @NotNull
    private LocalDate releaseDate;
    
    private LocalDate endDate;
    
    @Size(max = 100)
    private String coo;
} 