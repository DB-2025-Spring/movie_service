package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.MovieActor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieActorRepository extends JpaRepository<MovieActor, MovieActor.MovieActorId> {
    // 영화별 배우 조회
    List<MovieActor> findByMovie_MovieId(Long movieId);

    // 배우별 영화 조회
    List<MovieActor> findByActor_ActorID(Long actorId);
}
