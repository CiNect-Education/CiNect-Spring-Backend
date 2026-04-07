package com.cinect.service;

import com.cinect.dto.request.AdminNewsPatchRequest;
import com.cinect.dto.request.AdminNewsRequest;
import com.cinect.dto.response.NewsResponse;
import com.cinect.entity.NewsArticle;
import com.cinect.entity.enums.NewsCategory;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsArticleRepository newsArticleRepository;

    /** Resolve related articles by comma-separated UUIDs (same contract as NestJS {@code ?ids=}). */
    public List<NewsResponse> findAllByIdsCsv(String idsCsv) {
        List<UUID> order = new ArrayList<>();
        for (String part : idsCsv.split(",")) {
            String s = part.trim();
            if (s.isEmpty()) {
                continue;
            }
            try {
                order.add(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {
                // skip invalid tokens (matches Nest lenient behaviour)
            }
        }
        if (order.isEmpty()) {
            return List.of();
        }
        var found = newsArticleRepository.findAllById(order);
        var byId = found.stream().collect(Collectors.toMap(NewsArticle::getId, java.util.function.Function.identity()));
        return order.stream().map(byId::get).filter(Objects::nonNull).map(this::toResponse).collect(Collectors.toList());
    }

    /** @param pageIndex 0-based Spring Data page index */
    public Page<NewsResponse> findAll(NewsCategory category, int pageIndex, int limit) {
        Pageable pageable = PageRequest.of(pageIndex, limit);
        var result = category != null
                ? newsArticleRepository.findByCategoryOrderByPublishedAtDesc(category, pageable)
                : newsArticleRepository.findAllByOrderByPublishedAtDesc(pageable);
        return result.map(this::toResponse);
    }

    public NewsResponse findBySlug(String slug) {
        var n = newsArticleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("News article not found"));
        return toResponse(n);
    }

    /** Admin: paginated list (1-based page handled in controller). */
    @Transactional(readOnly = true)
    public Page<NewsResponse> findAllForAdmin(int pageIndex, int limit) {
        Pageable pageable = PageRequest.of(pageIndex, limit);
        return newsArticleRepository.findAllByOrderByPublishedAtDesc(pageable).map(this::toResponse);
    }

    @Transactional
    public NewsResponse createForAdmin(AdminNewsRequest req) {
        if (newsArticleRepository.existsBySlug(req.getSlug())) {
            throw new BadRequestException("Slug already in use");
        }
        List<UUID> rel = List.of();
        if (req.getRelatedArticleIds() != null && !req.getRelatedArticleIds().isEmpty()) {
            rel = req.getRelatedArticleIds().stream().map(UUID::fromString).collect(Collectors.toList());
        }
        var a = NewsArticle.builder()
                .title(req.getTitle())
                .slug(req.getSlug())
                .excerpt(req.getExcerpt())
                .content(req.getContent())
                .category(req.getCategory())
                .imageUrl(req.getImageUrl())
                .author(req.getAuthor())
                .tags(req.getTags() != null ? req.getTags() : List.of())
                .relatedArticleIds(rel.isEmpty() ? null : rel)
                .publishedAt(parseInstantFlexible(req.getPublishedAt()))
                .build();
        a = newsArticleRepository.save(a);
        return toResponse(a);
    }

    @Transactional
    public NewsResponse updateForAdmin(UUID id, AdminNewsPatchRequest p) {
        var n = newsArticleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News article not found"));
        if (p.getSlug() != null && !p.getSlug().equals(n.getSlug())
                && newsArticleRepository.existsBySlugAndIdNot(p.getSlug(), id)) {
            throw new BadRequestException("Slug already in use");
        }
        if (p.getTitle() != null) n.setTitle(p.getTitle());
        if (p.getSlug() != null) n.setSlug(p.getSlug());
        if (p.getExcerpt() != null) n.setExcerpt(p.getExcerpt());
        if (p.getContent() != null) n.setContent(p.getContent());
        if (p.getCategory() != null) n.setCategory(p.getCategory());
        if (p.getImageUrl() != null) n.setImageUrl(p.getImageUrl());
        if (p.getAuthor() != null) n.setAuthor(p.getAuthor());
        if (p.getTags() != null) n.setTags(p.getTags());
        if (p.getRelatedArticleIds() != null) {
            var rel = p.getRelatedArticleIds().stream().map(UUID::fromString).collect(Collectors.toList());
            n.setRelatedArticleIds(rel.isEmpty() ? null : rel);
        }
        if (p.getPublishedAt() != null) {
            n.setPublishedAt(parseInstantFlexible(p.getPublishedAt()));
        }
        n = newsArticleRepository.save(n);
        return toResponse(n);
    }

    @Transactional
    public void deleteForAdmin(UUID id) {
        if (!newsArticleRepository.existsById(id)) {
            throw new ResourceNotFoundException("News article not found");
        }
        newsArticleRepository.deleteById(id);
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

    private NewsResponse toResponse(NewsArticle n) {
        List<String> relatedIds = null;
        if (n.getRelatedArticleIds() != null && !n.getRelatedArticleIds().isEmpty()) {
            relatedIds = n.getRelatedArticleIds().stream().map(UUID::toString).collect(Collectors.toList());
        }
        return NewsResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .slug(n.getSlug())
                .excerpt(n.getExcerpt())
                .content(n.getContent())
                .category(n.getCategory())
                .imageUrl(n.getImageUrl())
                .author(n.getAuthor())
                .tags(n.getTags())
                .relatedArticleIds(relatedIds)
                .publishedAt(n.getPublishedAt())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
