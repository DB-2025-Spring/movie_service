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
    @SequenceGenerator(name="card_seq", sequenceName = "card_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq")
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
