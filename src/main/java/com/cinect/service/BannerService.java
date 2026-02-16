package com.cinect.service;

import com.cinect.dto.response.BannerResponse;
import com.cinect.entity.Banner;
import com.cinect.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

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
}
