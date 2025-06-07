package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.MovieActor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieActorRepository extends JpaRepository<MovieActor, MovieActor.MovieActorId> {
    // 영화 기준 배우 목록
    List<MovieActor> findByMovie_MovieId(Long movieId);

    // 배우 기준 출연 영화
    List<MovieActor> findByActor_ActorId(Long actorId);
}
