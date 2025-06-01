package com.dbs.movieservice.domain.member;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="card")
public class Card {
    @Id
    @Column(name="card_id")
    private Long cardId;

    @Column(length=100, name="card_company")
    private String cardCompany;
    @Column(name = "balance")
    private int balance;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="customer_id", nullable = false)
    private Customer customer;

    // 잔액 증가 메서드
    public void addBalance(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("잔액 증가 금액은 음수일 수 없습니다.");
        }
        this.balance += amount;
    }

    // 잔액 차감 메서드
    public void deductBalance(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("잔액 차감 금액은 음수일 수 없습니다.");
        }
        if (this.balance < amount) {
            throw new IllegalStateException("카드 잔액이 부족합니다. 현재 잔액: " + this.balance + ", 차감 요청 금액: " + amount);
        }
        this.balance -= amount;
    }

    // 잔액 확인 메서드
    public boolean hasEnoughBalance(int amount) {
        return this.balance >= amount;
    }

    // 충전 메서드 (addBalance의 별칭)
    public void chargeBalance(int amount) {
        addBalance(amount);
    }
}
