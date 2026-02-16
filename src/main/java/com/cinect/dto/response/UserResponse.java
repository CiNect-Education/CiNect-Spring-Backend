package com.cinect.dto.response;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private String role;
    private String membershipTier;
    private Integer membershipPoints;
    private LocalDate dateOfBirth;
    private String gender;
    private String city;
    private Boolean isActive;
    private Boolean emailVerified;
    private Instant createdAt;
    private Instant updatedAt;
}
