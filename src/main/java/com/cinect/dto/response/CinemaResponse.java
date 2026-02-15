package com.cinect.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CinemaResponse {
    private UUID id;
    private String name;
    private String slug;
    private String address;
    private String city;
    private String district;
    private String phone;
    private String email;
    private String imageUrl;
    private List<String> amenities;
    private Double latitude;
    private Double longitude;
    private Boolean isActive;
    private Instant createdAt;
}
