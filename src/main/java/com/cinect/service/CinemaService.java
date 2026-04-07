package com.cinect.service;

import com.cinect.dto.request.CreateCinemaRequest;
import com.cinect.dto.request.UpdateCinemaRequest;
import com.cinect.dto.response.CinemaResponse;
import com.cinect.dto.response.RoomResponse;
import com.cinect.entity.Cinema;
import com.cinect.entity.Room;
import com.cinect.exception.BadRequestException;
import com.cinect.exception.ResourceNotFoundException;
import com.cinect.entity.ProvinceNew;
import com.cinect.repository.CinemaRepository;
import com.cinect.repository.ProvinceNewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CinemaService {

    private final CinemaRepository cinemaRepository;
    private final ProvinceNewRepository provinceNewRepository;
    private final ProvinceService provinceService;

    /** Full list with nested rooms for admin UI (Nest-compatible). */
    @Transactional(readOnly = true)
    public List<CinemaResponse> findAllForAdmin() {
        return cinemaRepository.findAllActiveWithRooms().stream()
                .map(this::toResponseWithRooms)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CinemaResponse> findAll(String city, String search, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("name"));
        String provinceCode = provinceService.resolveToNewCode(city);
        String trimmedSearch = (search != null && !search.isBlank()) ? search.trim() : null;
        Page<Cinema> pageResult;
        if (provinceCode != null && trimmedSearch != null) {
            pageResult = cinemaRepository.findAllByProvinceCodeAndSearch(provinceCode, trimmedSearch, pageable);
        } else if (provinceCode != null) {
            pageResult = cinemaRepository.findAllByProvinceCode(provinceCode, pageable);
        } else if (trimmedSearch != null) {
            pageResult = cinemaRepository.findAllBySearch(trimmedSearch, pageable);
        } else {
            pageResult = cinemaRepository.findAllActive(pageable);
        }
        return pageResult.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CinemaResponse findBySlug(String slugOrId) {
        Cinema cinema = null;
        try {
            var id = UUID.fromString(slugOrId);
            cinema = cinemaRepository.findByIdAndIsActiveTrue(id).orElse(null);
        } catch (IllegalArgumentException ignored) {
            // not a UUID; resolve as slug below
        }
        if (cinema == null) {
            cinema = cinemaRepository.findBySlugAndIsActiveTrue(slugOrId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cinema not found: " + slugOrId));
        }
        return toResponse(cinema);
    }

    @Transactional
    public CinemaResponse create(CreateCinemaRequest req) {
        if (cinemaRepository.findBySlugAndIsActiveTrue(req.getSlug()).isPresent()) {
            throw new BadRequestException("Slug already exists");
        }
        ProvinceNew province = null;
        if (req.getProvinceNewId() != null) {
            province = provinceNewRepository.findById(req.getProvinceNewId())
                    .orElseThrow(() -> new BadRequestException("Invalid provinceNewId"));
        } else {
            String resolvedCode = provinceService.resolveToNewCode(req.getCity());
            if (resolvedCode != null) {
                province = provinceNewRepository.findByCode(resolvedCode).orElse(null);
            }
        }
        var cinema = Cinema.builder()
                .name(req.getName())
                .slug(req.getSlug())
                .address(req.getAddress())
                .city(req.getCity())
                .ward(req.getWard())
                .provinceNew(province)
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
        if (req.getWard() != null) cinema.setWard(req.getWard());
        if (req.getProvinceNewId() != null) {
            var p = provinceNewRepository.findById(req.getProvinceNewId())
                    .orElseThrow(() -> new BadRequestException("Invalid provinceNewId"));
            cinema.setProvinceNew(p);
        } else if (req.getCity() != null) {
            String resolvedCode = provinceService.resolveToNewCode(req.getCity());
            cinema.setProvinceNew(resolvedCode != null ? provinceNewRepository.findByCode(resolvedCode).orElse(null) : null);
        }
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
        int roomCount = c.getRooms() != null
                ? (int) c.getRooms().stream().filter(r -> r.getIsActive() != null && r.getIsActive()).count()
                : 0;

        return CinemaResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .slug(c.getSlug())
                .address(c.getAddress())
                .city(c.getCity())
                .ward(c.getWard())
                .provinceCode(c.getProvinceNew() != null ? c.getProvinceNew().getCode() : null)
                .district(c.getDistrict())
                .phone(c.getPhone())
                .email(c.getEmail())
                .imageUrl(c.getImageUrl())
                .amenities(c.getAmenities())
                .latitude(c.getLatitude())
                .longitude(c.getLongitude())
                .roomCount(roomCount)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private CinemaResponse toResponseWithRooms(Cinema c) {
        CinemaResponse base = toResponse(c);
        List<RoomResponse> rooms = c.getRooms() == null ? List.of() : c.getRooms().stream()
                .filter(r -> r.getIsActive() == null || r.getIsActive())
                .map(r -> roomToAdminSummary(c, r))
                .collect(Collectors.toList());
        base.setRooms(rooms);
        return base;
    }

    private RoomResponse roomToAdminSummary(Cinema c, Room r) {
        return RoomResponse.builder()
                .id(r.getId())
                .cinemaId(c.getId())
                .cinemaName(c.getName())
                .name(r.getName())
                .format(r.getFormat())
                .totalSeats(r.getTotalSeats())
                .rows(r.getRows())
                .columns(r.getColumns())
                .isActive(r.getIsActive())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
