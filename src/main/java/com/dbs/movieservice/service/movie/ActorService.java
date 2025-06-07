package com.dbs.movieservice.service.movie;

import com.dbs.movieservice.domain.movie.Actor;
import com.dbs.movieservice.domain.movie.Movie;
import com.dbs.movieservice.domain.movie.MovieActor;
import com.dbs.movieservice.dto.ActorDto;
import com.dbs.movieservice.dto.MovieDto;
import com.dbs.movieservice.repository.movie.ActorRepository;
import com.dbs.movieservice.repository.movie.MovieActorRepository;
import com.dbs.movieservice.repository.movie.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActorService {

    private final ActorRepository actorRepository;
    private final MovieActorRepository movieActorRepository;

    // 배우 이름으로 검색
    @Transactional(readOnly = true)
    public List<ActorDto> searchActorsByName(String actorName) {
        return actorRepository.findByActorName(actorName)
                .stream()
                .map(ActorDto::new)
                .toList();
    }

    // 해당 배우의 출연 영화 조회
    @Transactional(readOnly = true)
    public List<MovieDto> findMoviesByActor(Long actorId) {
        List<MovieActor> links = movieActorRepository.findByActor_ActorId(actorId);
        return links.stream()
                .map(MovieActor::getMovie)
                .map(MovieDto::new)
                .toList();
    }



    /**
     * 모든 배우 조회
     */
    @Transactional(readOnly = true)
    public List<Actor> findAllActors() {
        return actorRepository.findAll();
    }

    /**
     * ID로 배우 조회
     */
    @Transactional(readOnly = true)
    public Optional<Actor> findActorById(Long actorId) {
        return actorRepository.findById(actorId);
    }

    /**
     * 배우 저장
     */
    @Transactional
    public Actor saveActor(Actor actor) {
        return actorRepository.save(actor);
    }

    /**
     * 배우 수정
     */
    @Transactional
    public Actor updateActor(Long actorId, String actorName) {
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new RuntimeException("배우를 찾을 수 없습니다: " + actorId));
        
        actor.setActorName(actorName);
        
        return actorRepository.save(actor);
    }

    /**
     * 배우 삭제
     */
    @Transactional
    public void deleteActor(Long actorId) {
        if (!actorRepository.existsById(actorId)) {
            throw new RuntimeException("배우를 찾을 수 없습니다: " + actorId);
        }
        actorRepository.deleteById(actorId);
    }
} 