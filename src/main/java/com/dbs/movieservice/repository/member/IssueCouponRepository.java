package com.dbs.movieservice.repository.member;

import com.dbs.movieservice.domain.member.IssueCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueCouponRepository extends JpaRepository<IssueCoupon, IssueCoupon.IssueCouponId> {
}
