package com.cinect.entity;

import com.cinect.entity.enums.HoldStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "holds")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Hold {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "seat_ids", columnDefinition = "jsonb")
    private List<UUID> seatIds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "hold_status")
    @Builder.Default
    private HoldStatus status = HoldStatus.ACTIVE;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
