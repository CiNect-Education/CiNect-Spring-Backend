package com.cinect.service;

import com.cinect.dto.request.CreateMovieRequest;
import com.cinect.dto.request.UpdateMovieRequest;
import com.cinect.dto.response.MovieResponse;
import com.cinect.entity.Genre;
import com.cinect.entity.Movie;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    public org.springframework.data.domain.Page<MovieResponse> findAll(MovieStatus status, String search, UUID genreId,
                                                                       int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("releaseDate").descending());
        Page<Movie> pageResult;
        if (genreId != null) {
            pageResult = movieRepository.findByGenre(genreId, pageable);
        } else {
            pageResult = movieRepository.findAllFiltered(status, search == null || search.isBlank() ? null : search.trim(), pageable);
        }
        return pageResult.map(this::toResponse);
    }

    public MovieResponse findBySlug(String slug) {
        var movie = movieRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + slug));
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
                .director(req.getDirector())
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
                .cast(m.getCastMembers())
                .language(m.getLanguage())
                .subtitles(m.getSubtitles())
                .rating(m.getRating() != null ? m.getRating().doubleValue() : null)
                .ratingCount(m.getRatingCount())
                .ageRating(m.getAgeRating())
                .formats(m.getFormats())
                .status(m.getStatus())
                .genres(m.getGenres() != null ? m.getGenres().stream()
                        .map(g -> MovieResponse.GenreInfo.builder()
                                .id(g.getId())
                                .name(g.getName())
                                .slug(g.getSlug())
                                .build())
                        .collect(Collectors.toList()) : List.of())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
