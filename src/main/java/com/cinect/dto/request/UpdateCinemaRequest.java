package com.cinect.dto.request;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateCinemaRequest {
    private String name;
    private String slug;
    private String address;
    private String city;
    private String ward;
    private UUID provinceNewId;
    private String district;
    private String phone;
    private String email;
    private String imageUrl;
    private List<String> amenities;
    private Double latitude;
    private Double longitude;
    private Boolean isActive;
}
