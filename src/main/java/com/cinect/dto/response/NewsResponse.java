package com.cinect.dto.response;

import com.cinect.entity.enums.NewsCategory;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NewsResponse {
    private UUID id;
    private String title;
    private String slug;
    private String excerpt;
    private String content;
    private NewsCategory category;
    private String imageUrl;
    private String author;
    private List<String> tags;
    private Instant publishedAt;
    private Instant createdAt;
}
