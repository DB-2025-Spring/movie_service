package com.dbs.movieservice.domain.movie;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "movie_genre")
public class MovieGenre {
    @EmbeddedId
    private MovieGenreId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    @JsonBackReference
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("genreId")
    @JoinColumn(name = "genre_id")
    @JsonBackReference
    private Genre genre;

    public MovieGenre() {}

    public MovieGenre(Movie movie, Genre genre) {
        this.movie = movie;
        this.genre = genre;
        this.id = new MovieGenreId(movie.getMovieId(), genre.getGenreId());
    }




    @Embeddable
    public static class MovieGenreId implements Serializable {

        private Long movieId;
        private Long genreId;

        public MovieGenreId() {}

        public MovieGenreId(Long movieId, Long genreId) {
            this.movieId = movieId;
            this.genreId = genreId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MovieGenreId)) return false;
            MovieGenreId that = (MovieGenreId) o;
            return Objects.equals(movieId, that.movieId) &&
                    Objects.equals(genreId, that.genreId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(movieId, genreId);
        }
    }
}
