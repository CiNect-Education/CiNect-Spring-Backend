package com.cinect.entity;

import com.cinect.entity.enums.SupportCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "support_tickets")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "support_category")
    @Builder.Default
    private SupportCategory category = SupportCategory.OTHER;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "is_resolved", nullable = false)
    @Builder.Default
    private Boolean isResolved = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
