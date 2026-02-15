package com.cinect.entity;

import com.cinect.entity.enums.NewsCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "news_articles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, columnDefinition = "text")
    private String excerpt;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "news_category")
    @Builder.Default
    private NewsCategory category = NewsCategory.GENERAL;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private String author;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> tags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "related_article_ids", columnDefinition = "jsonb")
    private List<UUID> relatedArticleIds;

    @Column(name = "published_at", nullable = false)
    @Builder.Default
    private Instant publishedAt = Instant.now();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
