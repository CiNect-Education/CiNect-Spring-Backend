package com.cinect.dto.request;

import com.cinect.entity.enums.NewsCategory;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminNewsPatchRequest {
    private String title;
    private String slug;
    private String excerpt;
    private String content;
    private NewsCategory category;
    private String imageUrl;
    private String author;
    private List<String> tags;
    private List<String> relatedArticleIds;
    private String publishedAt;
}
