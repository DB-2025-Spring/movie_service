package com.dbs.movieservice.service.member;

import com.dbs.movieservice.domain.member.ClientLevel;
import com.dbs.movieservice.repository.member.ClientLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientLevelService {

    private final ClientLevelRepository clientLevelRepository;

    /**
     * 고객 등급 조회
     */
    @Transactional(readOnly = true)
    public List<ClientLevel> findAllLevels() {
        return clientLevelRepository.findAll();
    }

    /**
     * ID로 고객 등급 조회
     */
    @Transactional(readOnly = true)
    public Optional<ClientLevel> findLevelById(Integer levelId) {
        return clientLevelRepository.findById(levelId);
    }

    /**
     * 고객 등급 저장
     */
    @Transactional
    public ClientLevel saveLevel(ClientLevel clientLevel) {
        return clientLevelRepository.save(clientLevel);
    }

    /**
     * 고객 등급 수정
     */
    @Transactional
    public ClientLevel updateLevel(Integer levelId, String levelName, Double rewardRate) {
        ClientLevel clientLevel = clientLevelRepository.findById(levelId)
                .orElseThrow(() -> new RuntimeException("등급을 찾을 수 없습니다: " + levelId));
        
        clientLevel.setLevelName(levelName);
        clientLevel.setRewardRate(rewardRate);
        
        return clientLevelRepository.save(clientLevel);
    }
} 
