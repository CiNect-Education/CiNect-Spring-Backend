package com.cinect.controller;

import com.cinect.dto.response.ApiResponse;
import com.cinect.dto.response.NewsResponse;
import com.cinect.dto.response.PageMeta;
import com.cinect.entity.enums.NewsCategory;
import com.cinect.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NewsResponse>>> findAll(
            @RequestParam(required = false) NewsCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {
        var data = newsService.findAll(category, page, limit);
        var meta = PageMeta.builder()
                .page(page)
                .limit(limit)
                .total(data.getTotalElements())
                .totalPages(data.getTotalPages())
                .hasNext(data.hasNext())
                .hasPrev(data.hasPrevious())
                .build();
        return ResponseEntity.ok(ApiResponse.success(data.getContent(), meta));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<NewsResponse>> findBySlug(@PathVariable String slug) {
        var data = newsService.findBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
