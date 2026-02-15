package com.cinect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateCinemaRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String slug;
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    private String district;
    private String phone;
    private String email;
    private String imageUrl;
    private List<String> amenities;
    private Double latitude;
    private Double longitude;
}
