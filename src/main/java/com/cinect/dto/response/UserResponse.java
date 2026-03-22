package com.cinect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private String avatar;
    private String role;
    private List<String> roles;
    private MembershipDetails membership;
    private LocalDate dateOfBirth;
    private String gender;
    private String city;
    private Boolean isActive;
    private Boolean emailVerified;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MembershipDetails {
        private String tier;
        private Integer level;
        private Integer points;
    }
}
