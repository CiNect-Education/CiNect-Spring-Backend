package com.cinect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminCampaignRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String slug;
    private String description;
    private String content;
    private String imageUrl;
    @NotBlank
    private String startDate;
    @NotBlank
    private String endDate;
    private Boolean isActive;
}
