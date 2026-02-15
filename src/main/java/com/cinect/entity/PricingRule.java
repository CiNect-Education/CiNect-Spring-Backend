package com.cinect.entity;

import com.cinect.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pricing_rules")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PricingRule extends BaseEntity {

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", columnDefinition = "seat_type")
    private SeatType seatType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "room_format")
    private RoomFormat format;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_type", columnDefinition = "day_type")
    private DayType dayType;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_slot", columnDefinition = "time_slot")
    private TimeSlot timeSlot;

    @Column(name = "is_holiday")
    @Builder.Default
    private Boolean isHoliday = false;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
