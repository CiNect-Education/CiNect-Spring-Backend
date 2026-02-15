package com.cinect.service;

import com.cinect.dto.request.CreateCinemaRequest;
import com.cinect.dto.request.UpdateCinemaRequest;
import com.cinect.dto.response.CinemaResponse;
import com.cinect.entity.Cinema;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CinemaService {

    private final CinemaRepository cinemaRepository;

    public Page<CinemaResponse> findAll(String city, String search, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("name"));
        var pageResult = cinemaRepository.findAllFiltered(
                city == null || city.isBlank() ? null : city.trim(),
                search == null || search.isBlank() ? null : search.trim(),
                pageable);
        return pageResult.map(this::toResponse);
    }

    public CinemaResponse findBySlug(String slug) {
        var cinema = cinemaRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Cinema not found: " + slug));
        return toResponse(cinema);
    }

    @Transactional
    public CinemaResponse create(CreateCinemaRequest req) {
        if (cinemaRepository.findBySlugAndIsActiveTrue(req.getSlug()).isPresent()) {
            throw new BadRequestException("Slug already exists");
        }
        var cinema = Cinema.builder()
                .name(req.getName())
                .slug(req.getSlug())
                .address(req.getAddress())
                .city(req.getCity())
                .district(req.getDistrict())
                .phone(req.getPhone())
                .email(req.getEmail())
                .imageUrl(req.getImageUrl())
                .amenities(req.getAmenities())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .isActive(true)
                .build();
        cinema = cinemaRepository.save(cinema);
        return toResponse(cinema);
    }

    @Transactional
    public CinemaResponse update(UUID id, UpdateCinemaRequest req) {
        var cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cinema not found"));
        if (req.getName() != null) cinema.setName(req.getName());
        if (req.getSlug() != null) cinema.setSlug(req.getSlug());
        if (req.getAddress() != null) cinema.setAddress(req.getAddress());
        if (req.getCity() != null) cinema.setCity(req.getCity());
        if (req.getDistrict() != null) cinema.setDistrict(req.getDistrict());
        if (req.getPhone() != null) cinema.setPhone(req.getPhone());
        if (req.getEmail() != null) cinema.setEmail(req.getEmail());
        if (req.getImageUrl() != null) cinema.setImageUrl(req.getImageUrl());
        if (req.getAmenities() != null) cinema.setAmenities(req.getAmenities());
        if (req.getLatitude() != null) cinema.setLatitude(req.getLatitude());
        if (req.getLongitude() != null) cinema.setLongitude(req.getLongitude());
        if (req.getIsActive() != null) cinema.setIsActive(req.getIsActive());
        cinema = cinemaRepository.save(cinema);
        return toResponse(cinema);
    }

    private CinemaResponse toResponse(Cinema c) {
        return CinemaResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .slug(c.getSlug())
                .address(c.getAddress())
                .city(c.getCity())
                .district(c.getDistrict())
                .phone(c.getPhone())
                .email(c.getEmail())
                .imageUrl(c.getImageUrl())
                .amenities(c.getAmenities())
                .latitude(c.getLatitude())
                .longitude(c.getLongitude())
                .isActive(c.getIsActive())
                .roomCount(c.getRooms() != null ? c.getRooms().size() : 0)
                .createdAt(c.getCreatedAt())
                .build();
    }
}
