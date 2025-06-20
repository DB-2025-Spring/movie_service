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

import java.time.LocalDate;
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
     * 배우 이름으로 검색 후 생성 또는 업데이트
     * 1. 같은 이름의 배우가 없으면 새로 생성
     * 2. 같은 이름의 배우가 있고 생년월일이 다르면 새로 생성
     * 3. 같은 이름, 같은 생년월일의 배우가 있으면 기존 배우 반환
     */
    @Transactional
    public Actor createOrUpdateActor(String actorName, LocalDate birthDate) {
        List<Actor> existingActors = actorRepository.findByActorName(actorName);
        
        // 1. 같은 이름의 배우가 없는 경우 새로 생성
        if (existingActors.isEmpty()) {
            log.info("배우 '{}' 신규 등록", actorName);
            Actor newActor = new Actor();
            newActor.setActorName(actorName);
            newActor.setBirthDate(birthDate);
            return actorRepository.save(newActor);
        }
        
        // 2. 같은 이름, 같은 생년월일을 가진 배우가 있는지 확인
        for (Actor existingActor : existingActors) {
            // 생년월일이 같거나, 둘 다 null인 경우 기존 배우 반환
            if ((existingActor.getBirthDate() == null && birthDate == null) ||
                (existingActor.getBirthDate() != null && existingActor.getBirthDate().equals(birthDate))) {
                log.info("배우 '{}' 이미 존재함 (ID: {})", actorName, existingActor.getActorId());
                return existingActor;
            }
        }
        
        // 3. 같은 이름이지만 생년월일이 다른 경우 새로 생성
        log.info("배우 '{}' 동명이인으로 신규 등록", actorName);
        Actor newActor = new Actor();
        newActor.setActorName(actorName);
        newActor.setBirthDate(birthDate);
        return actorRepository.save(newActor);
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
    public Actor updateActor(Long actorId, String actorName, LocalDate birthDate) {
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new RuntimeException("배우를 찾을 수 없습니다: " + actorId));
        
        actor.setActorName(actorName);
        actor.setBirthDate(birthDate);
        
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