package com.dbs.movieservice.domain.theater;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "seat_available")
public class SeatAvailable {
    @EmbeddedId
    private SeatAvailableId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("scheduleId")
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("seatId")
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "is_booked", length = 1, nullable = false)
    private String isBooked;


    @Getter
    @Setter
    @Embeddable
    public static class SeatAvailableId implements Serializable {
        @Column(name = "schedule_id")
        private Long scheduleId;
        @Column(name = "seat_id")
        private Long seatId;

        public SeatAvailableId() {}

        public SeatAvailableId(Long scheduleId, Long seatId) {
            this.scheduleId = scheduleId;
            this.seatId = seatId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SeatAvailableId)) return false;
            SeatAvailableId that = (SeatAvailableId) o;
            return Objects.equals(scheduleId, that.scheduleId) &&
                    Objects.equals(seatId, that.seatId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(scheduleId, seatId);
        }
    }
}
