package com.dbs.movieservice.service.movie;

import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.domain.ticketing.Ticket;
import com.dbs.movieservice.dto.MovieDto;
import com.dbs.movieservice.repository.movie.MovieRepository;
import com.dbs.movieservice.service.ticketing.PaymentService;
import com.dbs.movieservice.service.ticketing.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

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

    //제목으로 조회
    public List<MovieDto> searchMoviesByKeyword(String keyword) {
        return movieRepository.searchMoviesByKeyword(keyword)
                .stream().map(MovieDto::new).toList();
    }

    //최신순으로 정렬?
    public List<MovieDto> getRecentMovies() {
        return movieRepository.findAllByOrderByReleaseDateDesc()
                .stream().map(MovieDto::new).toList();
    }

    //현재 상영작
    public List<MovieDto> getNowShowingMovies() {
        return movieRepository.findNowShowingMovies()
                .stream().map(MovieDto::new).toList();
    }

    //상영예고작
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

    /**
     *
     * @param customer
     * @return 유저가 본 영화 리스트를 전부 조회 (이때, Movie객체는 세세한 정보가 다 들어있음.
     */
    public Set<Movie> findMovieByCustomer(Customer customer) {
        List<Ticket> userTicketList = paymentService.getAllTicketsByCustomerId(customer);
        Set<Movie> userMovieList = new HashSet<>();
        for(Ticket ticket : userTicketList) {
            userMovieList.add(ticket.getSchedule().getMovie());
        }
        return userMovieList;
    }

    /**
     *
     * @param customer
     * @param movie
     * @return 유저가 해당 영화를 관람했는지 검사
     */
    public boolean checkCustomerWatchedMovie(Customer customer, Movie movie) {
        Set<Movie> userMovieList = findMovieByCustomer(customer);
        for(Movie movie1 : userMovieList) {
            if(movie1.getMovieId().equals(movie.getMovieId()))
                return true;
        }
        return false;
    }


    public List<Movie> isNowShowing(){
        return movieRepository.findNowShowingMovies();
    }
} 