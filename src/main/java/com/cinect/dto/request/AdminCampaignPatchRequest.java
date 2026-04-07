package com.cinect.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminCampaignPatchRequest {
    private String title;
    private String slug;
    private String description;
    private String content;
    private String imageUrl;
    private String startDate;
    private String endDate;
    private Boolean isActive;
}
