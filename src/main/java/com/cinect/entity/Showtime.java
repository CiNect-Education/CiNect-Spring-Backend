package com.cinect.entity;

import com.cinect.entity.converter.RoomFormatConverter;
import com.cinect.entity.enums.RoomFormat;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "showtimes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Showtime extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Convert(converter = RoomFormatConverter.class)
    @Column(nullable = false, columnDefinition = "room_format")
    @Builder.Default
    private RoomFormat format = RoomFormat._2D;

    private String language;
    private String subtitles;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "member_exclusive", nullable = false)
    @Builder.Default
    private Boolean memberExclusive = false;
}
