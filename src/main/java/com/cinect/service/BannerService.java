package com.cinect.service;

import com.cinect.dto.request.AdminBannerPatchRequest;
import com.cinect.dto.request.AdminBannerRequest;
import com.cinect.dto.response.BannerResponse;
import com.cinect.entity.Banner;
import com.cinect.entity.Campaign;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.BannerRepository;
import com.cinect.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final CampaignRepository campaignRepository;

    public List<BannerResponse> findAll(String position) {
        var now = Instant.now();
        List<Banner> banners;
        if (position != null && !position.isEmpty()) {
            banners = bannerRepository.findByPositionAndIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderBySortOrderAsc(position, now, now);
        } else {
            banners = bannerRepository.findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderBySortOrderAsc(now, now);
        }
        return banners.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private BannerResponse toResponse(Banner b) {
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
    public List<BannerResponse> findAllForAdmin() {
        var list = bannerRepository.findAllWithCampaignForAdmin();
        list.sort(Comparator
                .comparing((Banner b) -> b.getPosition() != null ? b.getPosition() : "")
                .thenComparing(b -> b.getSortOrder() != null ? b.getSortOrder() : 0));
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public BannerResponse createForAdmin(AdminBannerRequest req) {
        Campaign camp = resolveCampaign(req.getCampaignId());
        String title = req.getTitle() != null && !req.getTitle().isBlank() ? req.getTitle() : "Banner";
        var b = Banner.builder()
                .title(title)
                .imageUrl(req.getImageUrl())
                .linkUrl(req.getLinkUrl())
                .position(req.getPosition() != null ? req.getPosition() : "home")
                .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .campaign(camp)
                .startDate(parseInstantOrNull(req.getStartDate()))
                .endDate(parseInstantOrNull(req.getEndDate()))
                .build();
        b = bannerRepository.save(b);
        return toResponse(b);
    }

    @Transactional
    public BannerResponse updateForAdmin(UUID id, AdminBannerPatchRequest p) {
        var b = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found"));
        if (p.getTitle() != null) b.setTitle(p.getTitle().isBlank() ? "Banner" : p.getTitle());
        if (p.getImageUrl() != null) b.setImageUrl(p.getImageUrl());
        if (p.getLinkUrl() != null) b.setLinkUrl(p.getLinkUrl());
        if (p.getPosition() != null) b.setPosition(p.getPosition());
        if (p.getSortOrder() != null) b.setSortOrder(p.getSortOrder());
        if (p.getIsActive() != null) b.setIsActive(p.getIsActive());
        if (p.getCampaignId() != null) {
            b.setCampaign(resolveCampaign(p.getCampaignId()));
        }
        if (p.getStartDate() != null) b.setStartDate(parseInstantOrNull(p.getStartDate()));
        if (p.getEndDate() != null) b.setEndDate(parseInstantOrNull(p.getEndDate()));
        b = bannerRepository.save(b);
        return toResponse(b);
    }

    @Transactional
    public void deleteForAdmin(UUID id) {
        if (!bannerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Banner not found");
        }
        bannerRepository.deleteById(id);
    }

    private Campaign resolveCampaign(String campaignId) {
        if (campaignId == null || campaignId.isBlank()) {
            return null;
        }
        return campaignRepository.findById(UUID.fromString(campaignId)).orElse(null);
    }

    private static Instant parseInstantOrNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(s);
        } catch (Exception e) {
            return LocalDate.parse(s).atStartOfDay(ZoneOffset.UTC).toInstant();
        }
    }
}
