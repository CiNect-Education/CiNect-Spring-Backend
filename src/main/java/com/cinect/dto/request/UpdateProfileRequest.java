package com.cinect.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateProfileRequest {
    @Size(min = 2, max = 80, message = "Full name must be between 2 and 80 characters")
    @Pattern(regexp = "^[\\p{L}\\s'.-]+$", message = "Full name contains invalid characters")
    private String fullName;

    @Pattern(regexp = "^(\\+84|0)\\d{9}$", message = "Phone number must start with 0 or +84 and contain 10 digits")
    private String phone;

    @Size(max = 500, message = "Avatar URL is too long")
    @Pattern(regexp = "^(https?://).+$", message = "Avatar URL must start with http:// or https://")
    private String avatar;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER|PREFER_NOT_TO_SAY)$", message = "Gender is invalid")
    private String gender;

    @Size(min = 2, max = 80, message = "City must be between 2 and 80 characters")
    @Pattern(regexp = "^[\\p{L}\\s'.-]+$", message = "City contains invalid characters")
    private String city;
}
