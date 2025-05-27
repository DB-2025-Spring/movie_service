package com.dbs.movieservice.repository.member;

import com.dbs.movieservice.domain.member.ClientLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClientLevelRepository extends JpaRepository<ClientLevel, Integer> {

    // ClientLevel 테이블에서 levelId가 가장 작은 레코드(즉, 기본 등급)를 조회
    @Query("SELECT cl FROM ClientLevel cl WHERE cl.levelId = (SELECT MIN(c.levelId) FROM ClientLevel c)")
    ClientLevel findDefaultLevel();
}
