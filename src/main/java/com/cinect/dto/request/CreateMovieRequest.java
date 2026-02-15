package com.cinect.dto.request;

import com.cinect.entity.enums.AgeRating;
import com.cinect.entity.enums.MovieStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateMovieRequest {
    @NotBlank
    private String title;
    private String originalTitle;
    @NotBlank
    private String slug;
    @NotBlank
    private String description;
    @NotBlank
    private String posterUrl;
    private String bannerUrl;
    private String trailerUrl;
    private List<String> galleryUrls;
    @NotNull @Min(1)
    private Integer duration;
    @NotNull
    private LocalDate releaseDate;
    private LocalDate endDate;
    @NotBlank
    private String director;
    private List<String> castMembers;
    @Builder.Default
    private String language = "Vietnamese";
    private String subtitles;
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;
    @Builder.Default
    private Integer ratingCount = 0;
    @Builder.Default
    private AgeRating ageRating = AgeRating.P;
    private List<String> formats;
    @Builder.Default
    private MovieStatus status = MovieStatus.COMING_SOON;
    private Set<UUID> genreIds;
}
