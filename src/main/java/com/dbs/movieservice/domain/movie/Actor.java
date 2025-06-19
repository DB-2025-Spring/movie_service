package com.dbs.movieservice.domain.movie;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    
    @Column(name="birth_date")
    private LocalDate birthDate;

    @OneToMany(mappedBy = "actor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieActor> movieActors = new ArrayList<>();

}
