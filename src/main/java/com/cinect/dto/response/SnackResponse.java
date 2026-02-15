package com.cinect.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SnackResponse {
    private UUID id;
    private UUID cinemaId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean isActive;
    private Instant createdAt;
}
