package com.cinect.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PageMeta {
    private int page;
    private int limit;
    private long total;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrev;
}
