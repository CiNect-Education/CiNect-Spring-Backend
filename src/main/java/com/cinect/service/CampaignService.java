package com.cinect.service;

import com.cinect.dto.response.BannerResponse;
import com.cinect.dto.response.CampaignResponse;
import com.cinect.entity.Banner;
import com.cinect.entity.Campaign;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public List<CampaignResponse> findActive() {
        var now = Instant.now();
        var campaigns = campaignRepository.findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(now, now);
        return campaigns.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public CampaignResponse findBySlug(String slug) {
        var campaign = campaignRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        return toResponse(campaign);
    }

    private CampaignResponse toResponse(Campaign c) {
        return CampaignResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .slug(c.getSlug())
                .description(c.getDescription())
                .content(c.getContent())
                .imageUrl(c.getImageUrl())
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .isActive(c.getIsActive())
                .metadata(c.getMetadata())
                .banners(c.getBanners() != null ? c.getBanners().stream().map(this::toBannerResponse).collect(Collectors.toList()) : List.of())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private BannerResponse toBannerResponse(Banner b) {
        return BannerResponse.builder()
                .id(b.getId())
                .title(b.getTitle())
                .imageUrl(b.getImageUrl())
                .linkUrl(b.getLinkUrl())
                .position(b.getPosition())
                .sortOrder(b.getSortOrder())
                .isActive(b.getIsActive())
                .campaignId(b.getCampaign() != null ? b.getCampaign().getId() : null)
                .startDate(b.getStartDate())
                .endDate(b.getEndDate())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
