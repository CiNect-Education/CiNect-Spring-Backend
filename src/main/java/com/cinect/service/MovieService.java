package com.cinect.service;

import com.cinect.dto.request.CreateMovieRequest;
import com.cinect.dto.request.UpdateMovieRequest;
import com.cinect.dto.response.MovieResponse;
import com.cinect.entity.Genre;
import com.cinect.entity.Movie;
import com.cinect.entity.enums.AgeRating;
import com.cinect.entity.enums.MovieStatus;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.GenreRepository;
import com.cinect.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    /** Unpaged admin list (Nest-compatible; avoids default page size 20). */
    @Transactional(readOnly = true)
    public List<MovieResponse> findAllForAdmin() {
        return findAll(null, null, null, null, null, null, null, null, "releaseDate:desc", 0, 50_000).getContent();
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<MovieResponse> findAll(
            MovieStatus status,
            String search,
            String genre,
            String language,
            AgeRating ageRating,
            Integer durationMin,
            Integer durationMax,
            String format,
            String sort,
            int page,
            int limit
    ) {
        int safeLimit = Math.max(1, limit);
        int safePage = Math.max(0, page);
        String trimmedSearch = (search != null && !search.isBlank()) ? search.trim().toLowerCase(Locale.ROOT) : null;
        String trimmedGenre = (genre != null && !genre.isBlank()) ? genre.trim().toLowerCase(Locale.ROOT) : null;
        String trimmedLanguage = (language != null && !language.isBlank()) ? language.trim().toLowerCase(Locale.ROOT) : null;
        String trimmedFormat = (format != null && !format.isBlank()) ? format.trim().toLowerCase(Locale.ROOT) : null;

        var allMovies = movieRepository.findAllActive(PageRequest.of(0, 10_000, Sort.by("releaseDate").descending())).getContent();

        Comparator<Movie> comparator = parseSort(sort);
        var filtered = allMovies.stream()
                .filter(m -> status == null || m.getStatus() == status)
                .filter(m -> trimmedSearch == null
                        || (m.getTitle() != null && m.getTitle().toLowerCase(Locale.ROOT).contains(trimmedSearch))
                        || (m.getOriginalTitle() != null && m.getOriginalTitle().toLowerCase(Locale.ROOT).contains(trimmedSearch)))
                .filter(m -> trimmedGenre == null || m.getGenres().stream().anyMatch(g ->
                        (g.getSlug() != null && g.getSlug().toLowerCase(Locale.ROOT).contains(trimmedGenre))
                                || (g.getName() != null && g.getName().toLowerCase(Locale.ROOT).contains(trimmedGenre))
                                || g.getId().toString().equalsIgnoreCase(trimmedGenre)))
                .filter(m -> trimmedLanguage == null || (m.getLanguage() != null
                        && m.getLanguage().toLowerCase(Locale.ROOT).contains(trimmedLanguage)))
                .filter(m -> ageRating == null || m.getAgeRating() == ageRating)
                .filter(m -> durationMin == null || (m.getDuration() != null && m.getDuration() >= durationMin))
                .filter(m -> durationMax == null || (m.getDuration() != null && m.getDuration() <= durationMax))
                .filter(m -> trimmedFormat == null || (m.getFormats() != null
                        && m.getFormats().stream().anyMatch(f -> f != null && f.toLowerCase(Locale.ROOT).equals(trimmedFormat))))
                .sorted(comparator)
                .toList();

        int from = Math.min(filtered.size(), safePage * safeLimit);
        int to = Math.min(filtered.size(), from + safeLimit);
        var pageItems = filtered.subList(from, to).stream().map(this::toResponse).toList();
        return new org.springframework.data.domain.PageImpl<>(
                pageItems,
                PageRequest.of(safePage, safeLimit),
                filtered.size()
        );
    }

    private Comparator<Movie> parseSort(String sort) {
        String value = (sort == null || sort.isBlank()) ? "releaseDate:desc" : sort.trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case "releasedate:asc" -> Comparator.comparing(Movie::getReleaseDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case "title:asc" -> Comparator.comparing(m -> safe(m.getTitle()));
            case "title:desc" -> Comparator.comparing((Movie m) -> safe(m.getTitle())).reversed();
            case "rating:asc" -> Comparator.comparing(m -> m.getRating() != null ? m.getRating() : java.math.BigDecimal.ZERO);
            case "rating:desc" -> Comparator.comparing((Movie m) -> m.getRating() != null ? m.getRating() : java.math.BigDecimal.ZERO).reversed();
            default -> Comparator.comparing(Movie::getReleaseDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        };
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    @Transactional(readOnly = true)
    public MovieResponse findBySlug(String slug) {
        Movie movie = null;
        try {
            UUID id = UUID.fromString(slug);
            movie = movieRepository.findById(id)
                    .filter(m -> !Boolean.TRUE.equals(m.getIsDeleted()))
                    .orElse(null);
        } catch (IllegalArgumentException ignored) {
            // Not a UUID, continue with slug lookup.
        }

        if (movie == null) {
            movie = movieRepository.findBySlugAndIsDeletedFalse(slug)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + slug));
        }
        return toResponse(movie);
    }

    @Transactional
    public MovieResponse create(CreateMovieRequest req) {
        if (movieRepository.findBySlugAndIsDeletedFalse(req.getSlug()).isPresent()) {
            throw new BadRequestException("Slug already exists");
        }
        var genres = resolveGenres(req.getGenreIds());
        var movie = Movie.builder()
                .title(req.getTitle())
                .originalTitle(req.getOriginalTitle())
                .slug(req.getSlug())
                .description(req.getDescription())
                .posterUrl(req.getPosterUrl())
                .bannerUrl(req.getBannerUrl())
                .trailerUrl(req.getTrailerUrl())
                .galleryUrls(req.getGalleryUrls())
                .duration(req.getDuration())
                .releaseDate(req.getReleaseDate())
                .endDate(req.getEndDate())
                .director(req.getDirector() != null && !req.getDirector().isBlank()
                        ? req.getDirector().trim()
                        : "—")
                .castMembers(req.getCastMembers())
                .language(req.getLanguage() != null ? req.getLanguage() : "Vietnamese")
                .subtitles(req.getSubtitles())
                .rating(req.getRating())
                .ratingCount(req.getRatingCount())
                .ageRating(req.getAgeRating())
                .formats(req.getFormats())
                .status(req.getStatus())
                .genres(genres)
                .build();
        movie = movieRepository.save(movie);
        return toResponse(movie);
    }

    @Transactional
    public MovieResponse update(UUID id, UpdateMovieRequest req) {
        var movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        if (movie.getIsDeleted()) {
            throw new ResourceNotFoundException("Movie not found");
        }
        if (req.getTitle() != null) movie.setTitle(req.getTitle());
        if (req.getOriginalTitle() != null) movie.setOriginalTitle(req.getOriginalTitle());
        if (req.getSlug() != null) movie.setSlug(req.getSlug());
        if (req.getDescription() != null) movie.setDescription(req.getDescription());
        if (req.getPosterUrl() != null) movie.setPosterUrl(req.getPosterUrl());
        if (req.getBannerUrl() != null) movie.setBannerUrl(req.getBannerUrl());
        if (req.getTrailerUrl() != null) movie.setTrailerUrl(req.getTrailerUrl());
        if (req.getGalleryUrls() != null) movie.setGalleryUrls(req.getGalleryUrls());
        if (req.getDuration() != null) movie.setDuration(req.getDuration());
        if (req.getReleaseDate() != null) movie.setReleaseDate(req.getReleaseDate());
        if (req.getEndDate() != null) movie.setEndDate(req.getEndDate());
        if (req.getDirector() != null) movie.setDirector(req.getDirector());
        if (req.getCastMembers() != null) movie.setCastMembers(req.getCastMembers());
        if (req.getLanguage() != null) movie.setLanguage(req.getLanguage());
        if (req.getSubtitles() != null) movie.setSubtitles(req.getSubtitles());
        if (req.getRating() != null) movie.setRating(req.getRating());
        if (req.getRatingCount() != null) movie.setRatingCount(req.getRatingCount());
        if (req.getAgeRating() != null) movie.setAgeRating(req.getAgeRating());
        if (req.getFormats() != null) movie.setFormats(req.getFormats());
        if (req.getStatus() != null) movie.setStatus(req.getStatus());
        if (req.getGenreIds() != null) movie.setGenres(resolveGenres(req.getGenreIds()));
        movie = movieRepository.save(movie);
        return toResponse(movie);
    }

    @Transactional
    public void softDelete(UUID id) {
        var movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        movie.setIsDeleted(true);
        movieRepository.save(movie);
    }

    private Set<Genre> resolveGenres(Set<UUID> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) return new HashSet<>();
        return new HashSet<>(genreRepository.findAllById(genreIds));
    }

    private MovieResponse toResponse(Movie m) {
        var genres = m.getGenres() != null
                ? m.getGenres().stream()
                    .map(g -> MovieResponse.GenreItem.builder()
                            .id(g.getId()).name(g.getName()).slug(g.getSlug()).build())
                    .collect(Collectors.toList())
                : List.<MovieResponse.GenreItem>of();

        var cast = m.getCastMembers() != null
                ? m.getCastMembers().stream()
                    .map(name -> MovieResponse.CastMember.builder()
                            .name(name).role("Actor").avatarUrl(null).build())
                    .collect(Collectors.toList())
                : List.<MovieResponse.CastMember>of();

        return MovieResponse.builder()
                .id(m.getId())
                .title(m.getTitle())
                .originalTitle(m.getOriginalTitle())
                .slug(m.getSlug())
                .description(m.getDescription())
                .posterUrl(m.getPosterUrl())
                .bannerUrl(m.getBannerUrl())
                .trailerUrl(m.getTrailerUrl())
                .galleryUrls(m.getGalleryUrls())
                .duration(m.getDuration())
                .releaseDate(m.getReleaseDate())
                .endDate(m.getEndDate())
                .director(m.getDirector())
                .cast(cast)
                .language(m.getLanguage())
                .subtitles(m.getSubtitles())
                .rating(m.getRating() != null ? m.getRating().doubleValue() : 0.0)
                .ratingCount(m.getRatingCount())
                .ageRating(m.getAgeRating())
                .formats(m.getFormats())
                .status(m.getStatus())
                .genres(genres)
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }
}
