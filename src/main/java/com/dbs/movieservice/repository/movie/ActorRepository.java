package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor, Long> {
}
