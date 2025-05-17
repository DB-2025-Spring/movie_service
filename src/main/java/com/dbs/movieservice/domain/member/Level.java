package com.dbs.movieservice.domain.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Level {
    @Id
    @SequenceGenerator(name = "level_seq", sequenceName = "level_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "level_seq")
    @Column(name="level_id")
    private int levelId;
    @Column(name="reward_rate")
    private Double rewardRate;
}
