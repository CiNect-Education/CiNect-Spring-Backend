package com.cinect.service;

import com.cinect.dto.response.NewsResponse;
import com.cinect.entity.NewsArticle;
import com.cinect.entity.enums.NewsCategory;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsArticleRepository newsArticleRepository;

    public Page<NewsResponse> findAll(NewsCategory category, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        var result = category != null
                ? newsArticleRepository.findByCategory(category, pageable)
                : newsArticleRepository.findAllByOrderByPublishedAtDesc(pageable);
        return result.map(this::toResponse);
    }

    public NewsResponse findBySlug(String slug) {
        var n = newsArticleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("News article not found"));
        return toResponse(n);
    }

    private NewsResponse toResponse(NewsArticle n) {
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
                .publishedAt(n.getPublishedAt())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
