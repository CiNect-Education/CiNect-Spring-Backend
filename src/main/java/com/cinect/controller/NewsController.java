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

    /** {@code page} is 1-based (same as NestJS {@code GET /news}). */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NewsResponse>>> findAll(
            @RequestParam(required = false) NewsCategory category,
            @RequestParam(required = false) String ids,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        if (ids != null && !ids.isBlank()) {
            var list = newsService.findAllByIdsCsv(ids);
            var meta = PageMeta.builder()
                    .page(1)
                    .limit(list.size())
                    .total(list.size())
                    .totalPages(1)
                    .hasNext(false)
                    .hasPrev(false)
                    .build();
            return ResponseEntity.ok(ApiResponse.success(list, meta));
        }
        int pageIndex = Math.max(0, page - 1);
        var data = newsService.findAll(category, pageIndex, limit);
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
