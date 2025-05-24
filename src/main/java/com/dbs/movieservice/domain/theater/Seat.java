package com.dbs.movieservice.domain.theater;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name="seat")
public class Seat {
    @Id
    @SequenceGenerator(name = "seat_seq", sequenceName = "seat_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seat_seq")
    @Column(name = "seat_id")
    private Long seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;
    @Column(name = "row_number", nullable = false)
    private Integer rowNumber;
    @Column(name = "column_number", nullable = false)
    private Integer columnNumber;
}
