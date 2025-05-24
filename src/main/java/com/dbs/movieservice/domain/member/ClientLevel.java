package com.dbs.movieservice.domain.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="client_level")
public class ClientLevel {
    @Id
    @Column(name="level_id")
    private Integer levelId;

    @Column(name="level_name", length = 50, nullable = false)
    private String levelName;

    @Column(name="reward_rate")
    private Double rewardRate;

    public ClientLevel(Integer levelId, String levelName, Double rewardRate) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.rewardRate = rewardRate;
    }
}
