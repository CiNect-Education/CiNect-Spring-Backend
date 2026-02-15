package com.cinect.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequest {
    @NotBlank @Email
    private String email;
    @NotBlank @Size(min = 6, max = 100)
    private String password;
    @NotBlank
    private String fullName;
    private String phone;
}
