package com.cinect.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminBannerPatchRequest {
    private String title;
    private String imageUrl;
    private String linkUrl;
    private String position;
    private Integer sortOrder;
    private Boolean isActive;
    private String campaignId;
    private String startDate;
    private String endDate;
}
