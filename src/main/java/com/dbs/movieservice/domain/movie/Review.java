package com.dbs.movieservice.domain.movie;

import com.dbs.movieservice.domain.member.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="review")
public class Review {
    @Id
    @SequenceGenerator(name="review_seq", sequenceName = "review_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_seq")
    @Column(name="review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="customer_id", nullable = false)
    private Customer customer;

    @Column(name="star_rating")
    private double starRating;

    @Lob
    @Column(name = "content_desc")
    private String contentDesc;

    @Column(name = "date_created")
    private LocalDate dateCreated;

}
