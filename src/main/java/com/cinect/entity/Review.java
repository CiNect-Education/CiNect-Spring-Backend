package com.cinect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, columnDefinition = "text")
    private String content;
}
