package com.cinect.dto.response;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private LocalDate dateOfBirth;
    private String gender;
    private String city;
    private Boolean isActive;
    private Boolean emailVerified;
    private Set<String> roles;
    private Instant createdAt;
}
