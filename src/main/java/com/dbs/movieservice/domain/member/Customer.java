package com.dbs.movieservice.domain.member;

import com.dbs.movieservice.domain.movie.Review;
import com.dbs.movieservice.domain.ticketing.Payment;
import com.dbs.movieservice.domain.ticketing.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "CUSTOMER")
public class Customer {
    @Id
    @SequenceGenerator(name="customer_seq",sequenceName = "customer_seq",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @Column(name="customer_id")
    private Long customerId;

    @Column(name = "customer_input_id", length = 100, nullable = false)
    private String customerInputId;

    @Column(name = "customer_pw", length = 100, nullable = false)
    private String customerPw;

    @Column(name = "customer_name", length = 100, nullable = false)
    private String customerName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "authority", length = 1)
    private String authority;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "points")
    private Integer points;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private ClientLevel level;

    // 관련 엔티티와의 관계 정의 및 CASCADE 설정
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssueCoupon> issuedCoupons = new ArrayList<>();

    // 포인트 증가 메서드
    public void addPoints(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("포인트 증가 금액은 음수일 수 없습니다.");
        }
        if (this.points == null) {
            this.points = 0;
        }
        this.points += amount;
    }

    // 포인트 차감 메서드
    public void deductPoints(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("포인트 차감 금액은 음수일 수 없습니다.");
        }
        if (this.points == null) {
            this.points = 0;
        }
        if (this.points < amount) {
            throw new IllegalStateException("보유 포인트가 부족합니다. 현재 포인트: " + this.points + ", 차감 요청 포인트: " + amount);
        }
        this.points -= amount;
    }

    // 포인트 잔액 확인 메서드
    public boolean hasEnoughPoints(int amount) {
        if (this.points == null) {
            return amount <= 0;
        }
        return this.points >= amount;
    }
}
