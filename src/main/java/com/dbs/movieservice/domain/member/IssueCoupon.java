package com.dbs.movieservice.domain.member;


import com.dbs.movieservice.domain.ticketing.Coupon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name="issue_cupon")
public class IssueCoupon {
    @Id
    @SequenceGenerator(name = "issue_coupon_seq", sequenceName = "issue_coupon_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "issue_coupon_seq")
    @Column(name = "issue_id")
    private Long issueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @CreationTimestamp
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    
    // 쿠폰 사용 처리 메서드
    public void useCoupon() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }
    
    // 쿠폰 사용 취소 메서드
    public void cancelUsage() {
        this.isUsed = false;
        this.usedAt = null;
    }
}
