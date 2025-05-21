package com.dbs.movieservice.domain.member;


import com.dbs.movieservice.domain.ticketing.Coupon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name="issue_cupon")
public class IssueCoupon {
    @EmbeddedId
    private IssueCouponId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerId")
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("couponId")
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;


    @Getter
    @Setter
    @Embeddable
    public static class IssueCouponId implements Serializable {

        private Long customerId;
        private Long couponId;

        public IssueCouponId() {}

        public IssueCouponId(Long customerId, Long couponId) {
            this.customerId = customerId;
            this.couponId = couponId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IssueCouponId)) return false;
            IssueCouponId that = (IssueCouponId) o;
            return Objects.equals(customerId, that.customerId) &&
                    Objects.equals(couponId, that.couponId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(customerId, couponId);
        }
    }
}
