package com.dbs.movieservice.domain.movie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@ToString
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movie_id;
    @Column(name = "view_rating", length = 10)
    private String viewRating;
    @Column(name = "movie_name", length = 100, nullable = false)
    private String movieName;
    @Column(name = "running_time")
    private Integer runningTime;
    @Column(name = "director_name", length = 50)
    private String directorName;
    @Lob
    @Column(name = "movie_desc")
    private String movieDesc;
    @Column(name = "distributor", length = 100)
    private String distributor;
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    @Column(name = "release_date")
    private LocalDate releaseDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "coo", length = 100)
    private String coo;

}
