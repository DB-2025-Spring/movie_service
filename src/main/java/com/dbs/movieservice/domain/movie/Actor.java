package com.dbs.movieservice.domain.movie;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Actor {
    @Id
    @SequenceGenerator(name="actor_seq", sequenceName = "actor_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="actor_seq")
    @Column(name="actor_id")
    private Long actorId;

    @Column(name="actor_name", length = 50)
    private String actorName;
}
