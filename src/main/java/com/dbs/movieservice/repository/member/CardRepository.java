package com.dbs.movieservice.repository.member;

import com.dbs.movieservice.domain.member.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
