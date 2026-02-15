package com.cinect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
}
