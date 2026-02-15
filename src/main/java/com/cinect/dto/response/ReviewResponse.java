package com.cinect.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewResponse {
    private UUID id;
    private UUID movieId;
    private UUID userId;
    private String userFullName;
    private Integer rating;
    private String content;
    private Instant createdAt;
}
