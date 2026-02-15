package com.cinect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResetPasswordRequest {
    @NotBlank
    private String token;
    @NotBlank @Size(min = 6, max = 100)
    private String newPassword;
}
