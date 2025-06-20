package com.dbs.movieservice.domain.movie;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name="Movie_Actor")
public class MovieActor {
    @EmbeddedId
    private MovieActorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("actorId")
    @JoinColumn(name = "actor_id")
    private Actor actor;

    public MovieActor() {}

    public MovieActor(Movie movie, Actor actor) {
        this.movie = movie;
        this.actor = actor;
        this.id = new MovieActorId(movie.getMovieId(), actor.getActorId());
    }


    @Getter
    @Setter
    @Embeddable
    public static class MovieActorId implements Serializable {

        private Long movieId;
        private Long actorId;

        public MovieActorId() {}

        public MovieActorId(Long movieId, Long actorId) {
            this.movieId = movieId;
            this.actorId = actorId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MovieActorId)) return false;
            MovieActorId that = (MovieActorId) o;
            return Objects.equals(movieId, that.movieId) &&
                    Objects.equals(actorId, that.actorId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(movieId, actorId);
        }
    }
}
