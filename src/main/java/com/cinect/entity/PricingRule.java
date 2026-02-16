package com.cinect.entity;

import com.cinect.entity.converter.RoomFormatConverter;
import com.cinect.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "seat_type", columnDefinition = "seat_type")
    private SeatType seatType;

    @Convert(converter = RoomFormatConverter.class)
    @Column(columnDefinition = "room_format")
    private RoomFormat format;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "day_type", columnDefinition = "day_type")
    private DayType dayType;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
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
