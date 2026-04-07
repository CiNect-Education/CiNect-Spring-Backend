package com.cinect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminBannerRequest {
    private String title;
    @NotBlank
    private String imageUrl;
    private String linkUrl;
    private String position;
    private Integer sortOrder;
    private Boolean isActive;
    private String campaignId;
    private String startDate;
    private String endDate;
}
