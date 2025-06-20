package com.dbs.movieservice.service.movie;

import com.dbs.movieservice.domain.movie.Actor;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.movie.MovieActor;
import com.dbs.movieservice.repository.movie.MovieActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieActorService {

    private final MovieActorRepository movieActorRepository;
    private final MovieService movieService;
    private final ActorService actorService;

    public MovieActor createMovieActor(Long movieId, Long actorId) {
        Movie movie = movieService.findMovieById(movieId)
                .orElseThrow(() -> new RuntimeException("해당 영화가 존재하지 않습니다."));
        Actor actor = actorService.findActorById(actorId)
                .orElseThrow(() -> new RuntimeException("해당 배우가 존재하지 않습니다."));

        MovieActor movieActor = new MovieActor(movie, actor);
        return movieActorRepository.save(movieActor);
    }
}
