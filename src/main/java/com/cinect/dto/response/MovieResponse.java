package com.cinect.dto.response;

import com.cinect.entity.enums.AgeRating;
import com.cinect.entity.enums.MovieStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MovieResponse {
    private UUID id;
    private String title;
    private String originalTitle;
    private String slug;
    private String description;
    private String posterUrl;
    private String bannerUrl;
    private String trailerUrl;
    private List<String> galleryUrls;
    private Integer duration;
    private LocalDate releaseDate;
    private LocalDate endDate;
    private String director;
    private List<String> cast;
    private String language;
    private String subtitles;
    private Double rating;
    private Integer ratingCount;
    private AgeRating ageRating;
    private List<String> formats;
    private MovieStatus status;
    private List<GenreInfo> genres;
    private Instant createdAt;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GenreInfo {
        private UUID id;
        private String name;
        private String slug;
    }
}
