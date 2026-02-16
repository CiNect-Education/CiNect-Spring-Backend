package com.cinect.dto.response;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BannerResponse {
    private UUID id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private String position;
    private Integer sortOrder;
    private Boolean isActive;
    private UUID campaignId;
    private Instant startDate;
    private Instant endDate;
    private Instant createdAt;
}
