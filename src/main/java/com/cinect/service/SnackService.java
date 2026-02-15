package com.cinect.service;

import com.cinect.dto.response.SnackResponse;
import com.cinect.entity.Snack;
import com.cinect.repository.SnackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SnackService {

    private final SnackRepository snackRepository;

    public List<SnackResponse> findAll() {
        return snackRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<SnackResponse> findByCinema(UUID cinemaId) {
        return snackRepository.findByCinema_IdAndIsActiveTrue(cinemaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private SnackResponse toResponse(Snack s) {
        return SnackResponse.builder()
                .id(s.getId())
                .cinemaId(s.getCinema() != null ? s.getCinema().getId() : null)
                .name(s.getName())
                .description(s.getDescription())
                .price(s.getPrice())
                .imageUrl(s.getImageUrl())
                .isActive(s.getIsActive())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
