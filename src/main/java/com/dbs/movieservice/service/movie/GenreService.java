package com.dbs.movieservice.service.movie;

import com.dbs.movieservice.domain.movie.Genre;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.dto.GenreDto;
import com.dbs.movieservice.dto.MovieDto;
import com.dbs.movieservice.repository.movie.GenreRepository;
import com.dbs.movieservice.repository.movie.MovieGenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    private final GenreRepository genreRepository;
    private final MovieGenreRepository movieGenreRepository;

    /**
     * 특정 장르에 속한 영화 조회
     */
    @Transactional(readOnly = true)
    public List<MovieDto> findMoviesByGenre(Long genreId) {
        List<Movie> movies = movieGenreRepository.findMoviesByGenreId(genreId);
        return movies.stream()
                .map((Movie movie) -> new MovieDto(movie))
                .toList();
    }

    /**
     * 모든 장르 조회
     */
    @Transactional(readOnly = true)
    public List<GenreDto> findAllGenres() {
        return genreRepository.findAll()
                .stream()
                .map(GenreDto::new)
                .toList();
    }

    /**
     * ID로 장르 조회
     */
    @Transactional(readOnly = true)
    public Optional<Genre> findGenreById(Long genreId) {
        return genreRepository.findById(genreId);
    }

    /**
     * 장르 저장
     */
    @Transactional
    public Genre saveGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    /**
     * 장르 수정
     */
    @Transactional
    public Genre updateGenre(Long genreId, String genreName) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("장르를 찾을 수 없습니다: " + genreId));
        
        genre.setGenreName(genreName);
        
        return genreRepository.save(genre);
    }

    /**
     * 장르 삭제
     */
    @Transactional
    public void deleteGenre(Long genreId) {
        if (!genreRepository.existsById(genreId)) {
            throw new RuntimeException("장르를 찾을 수 없습니다: " + genreId);
        }
        genreRepository.deleteById(genreId);
    }
} 