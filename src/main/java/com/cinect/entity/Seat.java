package com.cinect.entity;

import com.cinect.entity.enums.SeatStatus;
import com.cinect.entity.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "row_label", "number"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "row_label", nullable = false)
    private String rowLabel;

    @Column(nullable = false)
    private Integer number;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "seat_type")
    @Builder.Default
    private SeatType type = SeatType.STANDARD;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "seat_status")
    @Builder.Default
    private SeatStatus status = SeatStatus.AVAILABLE;

    @Column(name = "pair_id")
    private UUID pairId;

    @Column(name = "is_aisle", nullable = false)
    @Builder.Default
    private Boolean isAisle = false;

    private BigDecimal price;
}
