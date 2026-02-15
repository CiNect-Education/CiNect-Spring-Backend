package com.cinect.repository;

import com.cinect.entity.NewsArticle;
import com.cinect.entity.enums.NewsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, UUID> {
    Optional<NewsArticle> findBySlug(String slug);
    Page<NewsArticle> findByCategory(NewsCategory category, Pageable pageable);
    Page<NewsArticle> findAllByOrderByPublishedAtDesc(Pageable pageable);
}
