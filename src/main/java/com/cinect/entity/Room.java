package com.cinect.entity;

import com.cinect.entity.enums.RoomFormat;
import com.cinect.entity.converter.RoomFormatConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms", uniqueConstraints = @UniqueConstraint(columnNames = {"cinema_id", "name"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Room extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false)
    private String name;

    @Convert(converter = RoomFormatConverter.class)
    @Column(nullable = false, columnDefinition = "room_format")
    @Builder.Default
    private RoomFormat format = RoomFormat._2D;

    @Column(name = "total_seats", nullable = false)
    @Builder.Default
    private Integer totalSeats = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer rows = 0;

    @Column(name = "columns", nullable = false)
    @Builder.Default
    private Integer columns = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Seat> seats = new ArrayList<>();
}
