package com.cinect.dto.response;

import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CampaignResponse {
    private UUID id;
    private String title;
    private String slug;
    private String description;
    private String content;
    private String imageUrl;
    private Instant startDate;
    private Instant endDate;
    private Boolean isActive;
    private Object metadata;
    private List<BannerResponse> banners;
    private Instant createdAt;
    private Instant updatedAt;
}
