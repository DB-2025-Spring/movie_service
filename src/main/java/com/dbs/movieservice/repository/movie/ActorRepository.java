package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    // 이름으로 배우 검색(동명이인 고려)
    List<Actor> findByActorName(String actorName);
}
