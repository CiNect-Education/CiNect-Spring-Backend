package com.cinect.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Pattern(
            regexp = "^[A-Za-z0-9]+@gmail\\.com$",
            message = "Email must be in ten@gmail.com format and contain no special characters"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
            message = "Password must include uppercase, lowercase, number, and special character"
    )
    private String password;
}
