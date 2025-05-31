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
}
