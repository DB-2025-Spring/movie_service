package com.dbs.movieservice.service.movie;

import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.repository.movie.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;

    /**
     * 모든 영화 조회
     */
    @Transactional(readOnly = true)
    public List<Movie> findAllMovies() {
        return movieRepository.findAll();
    }

    /**
     * ID로 영화 조회
     */
    @Transactional(readOnly = true)
    public Optional<Movie> findMovieById(Long movieId) {
        return movieRepository.findById(movieId);
    }

    /**
     * 영화 저장
     */
    @Transactional
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    /**
     * 영화 수정
     */
    @Transactional
    public Movie updateMovie(Long movieId, String viewRating, String movieName, Integer runningTime,
                           String directorName, String movieDesc, String distributor, String imageUrl,
                           LocalDate releaseDate, LocalDate endDate, String coo) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("영화를 찾을 수 없습니다: " + movieId));
        
        movie.setViewRating(viewRating);
        movie.setMovieName(movieName);
        movie.setRunningTime(runningTime);
        movie.setDirectorName(directorName);
        movie.setMovieDesc(movieDesc);
        movie.setDistributor(distributor);
        movie.setImageUrl(imageUrl);
        movie.setReleaseDate(releaseDate);
        movie.setEndDate(endDate);
        movie.setCoo(coo);
        
        return movieRepository.save(movie);
    }

    /**
     * 영화 삭제
     */
    @Transactional
    public void deleteMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new RuntimeException("영화를 찾을 수 없습니다: " + movieId);
        }
        movieRepository.deleteById(movieId);
    }
} 