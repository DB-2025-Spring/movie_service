package com.dbs.movieservice.service.member;

import com.dbs.movieservice.config.exception.BusinessException;
import com.dbs.movieservice.domain.member.Card;
import com.dbs.movieservice.repository.member.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private final CardRepository cardRepository;


    public void updateBalance(Long cardId, int chargeMoney){
        Card lockedCard = cardRepository.findByCardIdWithLock(cardId);
        if(lockedCard.getBalance() < chargeMoney){
            throw new BusinessException(
                    "잔액이 부족합니다. 현재 잔액: " + lockedCard.getBalance() + ", 요청 금액: " + chargeMoney,
                    HttpStatus.BAD_REQUEST,
                    "INSUFFICIENT_BALANCE"
            );
        }
        lockedCard.setBalance(lockedCard.getBalance() - chargeMoney);
    }
}
