package com.dbs.movieservice.domain.theater;

import com.dbs.movieservice.domain.movie.Movie;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="schdule_id")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="theater_id")
    private Theater theater;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Column(name = "schedule_sequence", nullable = false)
    private Integer scheduleSequence;

    @Column(name = "schedule_start_time", nullable = false)
    private LocalDateTime scheduleStartTime;
}
