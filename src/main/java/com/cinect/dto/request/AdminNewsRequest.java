package com.cinect.dto.request;

import com.cinect.entity.enums.NewsCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminNewsRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String slug;
    @NotBlank
    private String excerpt;
    @NotBlank
    private String content;
    @NotNull
    private NewsCategory category;
    private String imageUrl;
    @NotBlank
    private String author;
    private List<String> tags;
    private List<String> relatedArticleIds;
    /** ISO-8601 instant string */
    private String publishedAt;
}
