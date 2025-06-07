package com.dbs.movieservice.service.movie;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.domain.ticketing.Ticket;
import com.dbs.movieservice.dto.MovieDto;
import com.dbs.movieservice.repository.movie.MovieRepository;
import com.dbs.movieservice.service.ticketing.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional (readOnly = true)
public class MovieService {

    private final MovieRepository movieRepository;
    private final PaymentService paymentService;
    //영화 키워드 검색
    public MovieDto getMovieDetail(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("영화를 찾을 수 없습니다."));
        return new MovieDto(movie);
    }

    //자동완성 (제목)

    public List<String> getAutoCompleteTitles(String keyword) {
        return movieRepository.findAutoCompleteTitles(keyword);
    }

    //최신 영화 목록
    public List<MovieDto> searchMoviesByKeyword(String keyword) {
        return movieRepository.searchMoviesByKeyword(keyword)
                .stream().map(MovieDto::new).toList();
    }

    //현재 상영작
    public List<MovieDto> getRecentMovies() {
        return movieRepository.findAllByOrderByReleaseDateDesc()
                .stream().map(MovieDto::new).toList();
    }

    //상영 예정작
    public List<MovieDto> getNowShowingMovies() {
        return movieRepository.findNowShowingMovies()
                .stream().map(MovieDto::new).toList();
    }

    //영화 상세
    public List<MovieDto> getUpcomingMovies() {
        return movieRepository.findUpcomingMovies()
                .stream().map(MovieDto::new).toList();
    }

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

//    public List<Movie> findMovieByCustomer(Customer customer) {
//        List<Payment> userPaymentList = paymentService.getApprovedPaymentsByCustomer(customer);
//        List<Ticket> userTicketList = new ArrayList<>();
//        for (Payment payment : userPaymentList) {
//            payment.getTickets().forEach(ticket -> userTicketList.add(ticket));
//        }
//        List<Movie> movieList = new ArrayList<>();
//        for (Ticket ticket : userTicketList) {
//            ticket.
//        }
//    }
} 