package com.dbs.movieservice.repository.member;

import com.dbs.movieservice.domain.member.Card;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface CardRepository extends JpaRepository<Card, Long> {
    /**
     * 이 부분은 write수준의 락킹을 걸어야합니다.
     * 예를들어, 내가 5000원 잔액이 있는 상태에서, a라는 트랜젝션이 3000원 감소, b라는 트랜젝션이 3000원 감소하는 일이 있다고 할 때
     * 1. a가 도착해서 유효한지 검사 및, read락 걸기
     * 2. 유효함, 잔액을 감소하기 직전
     * 3. b가 도착해서 유효함을 검사(read락만 걸려있기에 검사가능)
     * 4. b 트랜젝션도 유효함을 감지
     * 5. 둘 다 실행해서 오류 발생.
     * @param cardId
     * @return
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Card c WHERE c.cardId = :cardId")
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "5000")
    })
    Card findByCardIdWithLock(@Param("cardId") Long cardId);
}
