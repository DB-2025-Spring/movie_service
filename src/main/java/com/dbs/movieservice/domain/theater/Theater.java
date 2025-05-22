package com.dbs.movieservice.domain.theater;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name="theater")
public class Theater {
    @Id
    @SequenceGenerator(name = "theater_seq", sequenceName = "theater_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "theater_seq")
    @Column(name = "theater_id")
    private Long theaterId;
    @Column(nullable = false, length = 100, name ="theater_name")
    private String theaterName;
    @Column(nullable = false, name="total_seats")
    private int totalSeats;
}
