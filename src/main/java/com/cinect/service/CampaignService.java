package com.cinect.service;

import com.cinect.dto.request.AdminCampaignPatchRequest;
import com.cinect.dto.request.AdminCampaignRequest;
import com.cinect.dto.response.BannerResponse;
import com.cinect.dto.response.CampaignResponse;
import com.cinect.entity.Banner;
import com.cinect.entity.Campaign;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
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

    @Transactional(readOnly = true)
    public List<CampaignResponse> findAllForAdmin() {
        var raw = campaignRepository.findAllWithBannersForAdmin();
        var byId = new LinkedHashMap<UUID, Campaign>();
        for (Campaign c : raw) {
            byId.putIfAbsent(c.getId(), c);
        }
        var campaigns = new ArrayList<>(byId.values());
        campaigns.sort((a, b) -> {
            Instant sa = a.getStartDate() != null ? a.getStartDate() : Instant.EPOCH;
            Instant sb = b.getStartDate() != null ? b.getStartDate() : Instant.EPOCH;
            return sb.compareTo(sa);
        });
        return campaigns.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CampaignResponse createForAdmin(AdminCampaignRequest req) {
        if (campaignRepository.existsBySlug(req.getSlug())) {
            throw new BadRequestException("Campaign slug already in use");
        }
        var c = Campaign.builder()
                .title(req.getTitle())
                .slug(req.getSlug())
                .description(req.getDescription())
                .content(req.getContent())
                .imageUrl(req.getImageUrl())
                .startDate(parseInstantFlexible(req.getStartDate()))
                .endDate(parseInstantFlexible(req.getEndDate()))
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .metadata(new HashMap<>())
                .build();
        c = campaignRepository.save(c);
        return toResponse(c);
    }

    @Transactional
    public CampaignResponse updateForAdmin(UUID id, AdminCampaignPatchRequest p) {
        var c = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        if (p.getSlug() != null && !p.getSlug().equals(c.getSlug())
                && campaignRepository.existsBySlugAndIdNot(p.getSlug(), id)) {
            throw new BadRequestException("Campaign slug already in use");
        }
        if (p.getTitle() != null) c.setTitle(p.getTitle());
        if (p.getSlug() != null) c.setSlug(p.getSlug());
        if (p.getDescription() != null) c.setDescription(p.getDescription());
        if (p.getContent() != null) c.setContent(p.getContent());
        if (p.getImageUrl() != null) c.setImageUrl(p.getImageUrl());
        if (p.getStartDate() != null) c.setStartDate(parseInstantFlexible(p.getStartDate()));
        if (p.getEndDate() != null) c.setEndDate(parseInstantFlexible(p.getEndDate()));
        if (p.getIsActive() != null) c.setIsActive(p.getIsActive());
        c = campaignRepository.save(c);
        return toResponse(c);
    }

    @Transactional
    public void deactivateForAdmin(UUID id) {
        var c = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        c.setIsActive(false);
        campaignRepository.save(c);
    }

    private static Instant parseInstantFlexible(String s) {
        if (s == null || s.isBlank()) {
            return Instant.now();
        }
        try {
            return Instant.parse(s);
        } catch (Exception e) {
            return LocalDate.parse(s).atStartOfDay(ZoneOffset.UTC).toInstant();
        }
    }
}
