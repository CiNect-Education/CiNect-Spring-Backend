package com.cinect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "hold_seats")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@IdClass(HoldSeatId.class)
public class HoldSeat {

    @Id
    @Column(name = "hold_id")
    private UUID holdId;

    @Id
    @Column(name = "seat_id")
    private UUID seatId;

    @Column(name = "showtime_id", nullable = false)
    private UUID showtimeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hold_id", insertable = false, updatable = false)
    private Hold hold;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", insertable = false, updatable = false)
    private Seat seat;
}
