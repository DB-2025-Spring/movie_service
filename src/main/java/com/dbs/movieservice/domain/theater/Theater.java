package com.dbs.movieservice.domain.theater;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
public class Theater {
    @Id
    @SequenceGenerator(name = "theater_seq", sequenceName = "theater_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "theater_seq")
    @Column(name = "theater_id")
    private Long theaterId;
    @Column(nullable = false, length = 100)
    private String theater_name;
    private int total_seats;
}
