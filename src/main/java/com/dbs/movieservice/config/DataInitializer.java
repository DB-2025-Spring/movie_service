package com.dbs.movieservice.config;

import com.dbs.movieservice.domain.member.ClientLevel;
import com.dbs.movieservice.repository.member.ClientLevelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 애플리케이션 시작 시 기본 데이터를 초기화하는 컴포넌트
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ClientLevelRepository clientLevelRepository;

    @Override
    @Transactional
    public void run(String... args) {
        initClientLevels();
    }

    /**
     * 고객 등급 기본 데이터 초기화
     * 1: 브론즈(기본 등급), 2: 실버, 3: 골드, 4: 플래티넘, 5: 다이아몬드
     */
    private void initClientLevels() {
        // 기존 등급 데이터가 있는지 확인
        long count = clientLevelRepository.count();
        
        if (count == 0) {
            log.info("고객 등급 기본 데이터를 초기화합니다.");
            
            List<ClientLevel> defaultLevels = Arrays.asList(
                new ClientLevel(1, "BASIC", 0.00),   // 1: 기본 등급
                new ClientLevel(2, "BRONZE", 0.02),  // 2: 브론즈 등급 (2% 적립)
                new ClientLevel(3, "SILVER", 0.03),  // 3: 실버 등급 (3% 적립)
                new ClientLevel(4, "GOLD", 0.04),    // 4: 골드 등급 (4% 적립)
                new ClientLevel(5, "DIAMOND", 0.05)  // 5: 다이아몬드 등급 (5% 적립)
            );
            
            clientLevelRepository.saveAll(defaultLevels);
            log.info("고객 등급 기본 데이터 초기화 완료: {} 개 등급 생성", defaultLevels.size());
        } else {
            log.info("고객 등급 데이터가 이미 존재합니다. 초기화를 건너뜁니다. 현재 등급 수: {}", count);
        }
    }
} 