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
    
    @Column(name = "theater_name", nullable = false, length = 100)
    private String theaterName;
    
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;
    
    @Column(name = "row_count")
    private Integer rows;
    
    @Column(name = "column_count")
    private Integer columns;
}
