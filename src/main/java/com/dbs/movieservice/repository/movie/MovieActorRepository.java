package com.dbs.movieservice.repository.movie;

import com.dbs.movieservice.domain.movie.MovieActor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieActorRepository extends JpaRepository<MovieActor, MovieActor.MovieActorId> {
}
