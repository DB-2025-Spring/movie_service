package com.dbs.movieservice.domain.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="client_level")
public class ClientLevel {
    @Id
    @SequenceGenerator(name = "level_seq", sequenceName = "level_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "level_seq")
    @Column(name="level_id")
    private Integer levelId;
    @Column(name="reward_rate")
    private Double rewardRate;
}
