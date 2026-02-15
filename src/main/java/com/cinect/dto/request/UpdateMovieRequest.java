package com.cinect.dto.request;

import com.cinect.entity.enums.AgeRating;
import com.cinect.entity.enums.MovieStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateMovieRequest {
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
    private List<String> castMembers;
    private String language;
    private String subtitles;
    private BigDecimal rating;
    private Integer ratingCount;
    private AgeRating ageRating;
    private List<String> formats;
    private MovieStatus status;
    private Set<UUID> genreIds;
}
