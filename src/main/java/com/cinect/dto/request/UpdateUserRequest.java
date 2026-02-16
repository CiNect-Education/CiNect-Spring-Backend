package com.cinect.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateUserRequest {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String avatar;
    private LocalDate dateOfBirth;
    private String gender;
    private String city;
    private Boolean isActive;
    private Boolean emailVerified;
    private String role;
}
